#!/usr/bin/env python3
"""
VoltMate: EV Range & Energy Consumption Deep Learning Model Training Script
Trains a compact neural network on physics-informed EV telemetry and exports to TensorFlow Lite (.tflite).

Features:
  - Current SoC (%)
  - Battery capacity (kWh)
  - Speed (km/h)
  - Elevation delta (meters)
  - Ambient temperature (°C)
  - Traffic density factor (1.0 - 1.4)
  - AC status (0 or 1)
  - Vehicle mass (kg)
  - Baseline vehicle efficiency (Wh/km)
  - Trip distance (km)

Target:
  - Energy consumption (kWh)
"""

import os
import numpy as np

def generate_synthetic_ev_data(num_samples=15000):
    np.random.seed(42)
    # Features
    soc = np.random.uniform(15.0, 95.0, num_samples) # %
    capacity = np.random.choice([40.5, 50.0, 60.0, 75.0], num_samples) # kWh
    speed = np.random.uniform(30.0, 110.0, num_samples) # km/h
    elevation = np.random.uniform(-400.0, 900.0, num_samples) # meters
    temp = np.random.uniform(5.0, 42.0, num_samples) # deg C
    traffic = np.random.uniform(1.0, 1.35, num_samples) # factor
    ac = np.random.choice([0.0, 1.0], num_samples, p=[0.25, 0.75]) # binary
    mass = np.random.uniform(1400.0, 2100.0, num_samples) # kg
    baseline_wh_km = np.random.uniform(130.0, 165.0, num_samples) # Wh/km
    distance = np.random.uniform(5.0, 180.0, num_samples) # km

    # Physics-informed ground truth energy calculation
    base_kwh = (distance * baseline_wh_km) / 1000.0
    gravity_kwh = np.where(
        elevation > 0,
        (mass * 9.81 * elevation) / (3.6e6 * 0.85),
        (mass * 9.81 * elevation * 0.45) / 3.6e6
    )
    delta_temp = np.maximum(0.0, temp - 22.0)
    ac_kw = np.where(ac > 0.5, 1.2 + 0.08 * delta_temp, 0.15)
    ac_kwh = ac_kw * (distance / speed)
    traffic_kwh = base_kwh * (traffic - 1.0) * 0.8

    # Noise
    noise = np.random.normal(0.0, 0.15, num_samples)
    energy_consumed = np.maximum(0.5, base_kwh + gravity_kwh + ac_kwh + traffic_kwh + noise)

    X = np.stack([
        soc, capacity, speed, elevation, temp,
        traffic, ac, mass, baseline_wh_km, distance
    ], axis=1)

    y = energy_consumed.reshape(-1, 1)
    return X, y

def train_and_export():
    print("[VoltMate ML] Generating physics-informed training data...")
    X, y = generate_synthetic_ev_data(12000)

    # Train / Test split
    split = int(0.85 * len(X))
    X_train, X_test = X[:split], X[split:]
    y_train, y_test = y[:split], y[split:]

    # Normalization
    mean = np.mean(X_train, axis=0)
    std = np.std(X_train, axis=0) + 1e-7
    X_train_norm = (X_train - mean) / std
    X_test_norm = (X_test - mean) / std

    try:
        import tensorflow as tf
        print(f"[VoltMate ML] TensorFlow Version: {tf.__version__}")

        model = tf.keras.Sequential([
            tf.keras.layers.Input(shape=(10,)),
            tf.keras.layers.Dense(64, activation='relu'),
            tf.keras.layers.Dense(32, activation='relu'),
            tf.keras.layers.Dense(16, activation='relu'),
            tf.keras.layers.Dense(1, activation='linear')
        ])

        model.compile(optimizer='adam', loss='mse', metrics=['mae'])
        print("[VoltMate ML] Training compact regression model...")
        history = model.fit(
            X_train_norm, y_train,
            epochs=25,
            batch_size=64,
            validation_split=0.15,
            verbose=1
        )

        test_loss, test_mae = model.evaluate(X_test_norm, y_test, verbose=0)
        print(f"[VoltMate ML] Test MAE: {test_mae:.3f} kWh")

        # Export to TFLite
        converter = tf.lite.TFLiteConverter.from_keras_model(model)
        converter.optimizations = [tf.lite.Optimize.DEFAULT]
        tflite_model = converter.convert()

        os.makedirs("models", exist_ok=True)
        tflite_path = "models/voltmate_range_predictor.tflite"
        with open(tflite_path, "wb") as f:
            f.write(tflite_model)
        print(f"[VoltMate ML] Exported quantized TFLite model to: {tflite_path} ({len(tflite_model)} bytes)")

        # Save normalization constants
        np.savez("models/norm_constants.npz", mean=mean, std=std)
        print("[VoltMate ML] Saved normalization statistics to models/norm_constants.npz")

    except ImportError:
        print("[VoltMate ML] Notice: TensorFlow not installed in current environment.")
        print("[VoltMate ML] In DEMO_MODE, VoltMate uses the deterministic Kotlin/TypeScript physics engine.")
        print("[VoltMate ML] Run `pip install tensorflow numpy` to execute model training and TFLite export.")

if __name__ == "__main__":
    train_and_export()
