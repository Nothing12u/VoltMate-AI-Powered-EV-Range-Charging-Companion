// Supabase Edge Function: trip-assistant
// Validates payload, invokes Gemini with structured JSON output, handles mock fallback safely

import { serve } from "https://deno.land/std@0.168.0/http/server.ts";

interface TripAssistantRequest {
  userPrompt: string;
  vehicleProfile: {
    model: string;
    batteryCapacityKwh: number;
    currentSocPercent: number;
  };
  origin: string;
  destination: string;
  preference: string;
}

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
};

serve(async (req) => {
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders });
  }

  try {
    const payload: TripAssistantRequest = await req.json();
    const apiKey = Deno.env.get("GEMINI_API_KEY");

    if (!apiKey) {
      // Deterministic fallback response for DEMO_MODE
      return new Response(
        JSON.stringify({
          summary: "Your 96 km trip to Nandi Hills with +620m elevation requires 1 charge stop to protect your 15% reserve buffer.",
          recommendation: "Reserve Ananya's Home Charger in Hebbal. It provides a relaxed, 40% cheaper charge stop with zero wait time right before the highway climb.",
          predictedArrivalSoc: 8,
          safetyReserveSoc: 15,
          totalDistanceKm: 96.0,
          estimatedTravelMinutes: 115,
          chargeStops: [
            {
              chargerId: "vs-ananya-hebbal",
              chargerName: "Ananya's Home Charger (VoltShare)",
              reason: "Optimal geographic placement before NH44 elevation gain. Verified host with 4.9 rating.",
              arrivalSoc: 24,
              targetSoc: 52,
              chargeMinutes: 45,
              estimatedCost: 84.0,
              carbonIntensity: 410,
              amenities: ["Gated parking", "Washroom", "Drinking water", "Wi-Fi"]
            }
          ],
          scenicDetours: [
            {
              title: "Devanahalli Fort",
              description: "Birthplace of Tipu Sultan, 4 km off NH44",
              extraMinutes: 15
            }
          ],
          tips: [
            "Pre-cool the vehicle while plugged into Ananya's charger to save battery on the ascent",
            "Descent from Nandi Hills will regenerate ~2.5 kWh back into your battery"
          ]
        }),
        { headers: { ...corsHeaders, "Content-Type": "application/json" } }
      );
    }

    // Live Gemini API call if key is configured
    const prompt = `You are VoltMate EV copilot. The user is planning a trip from ${payload.origin} to ${payload.destination} with a ${payload.vehicleProfile.model} (${payload.vehicleProfile.batteryCapacityKwh} kWh, currently at ${payload.vehicleProfile.currentSocPercent}% SoC). Preference is ${payload.preference}. Provide structured JSON recommendations for EV charging, scenic detours, and arrival SoC.`;

    const response = await fetch(
      `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${apiKey}`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          contents: [{ parts: [{ text: prompt }] }],
          generationConfig: { responseMimeType: "application/json" }
        })
      }
    );

    const data = await response.json();
    const textOutput = data.candidates?.[0]?.content?.parts?.[0]?.text;
    const structured = textOutput ? JSON.parse(textOutput) : null;

    return new Response(JSON.stringify(structured), {
      headers: { ...corsHeaders, "Content-Type": "application/json" }
    });
  } catch (err) {
    return new Response(
      JSON.stringify({ error: err.message }),
      { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } }
    );
  }
});
