// Supabase Edge Function: calculate-booking-price
import { serve } from "https://deno.land/std@0.168.0/http/server.ts";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
};

serve(async (req) => {
  if (req.method === "OPTIONS") return new Response("ok", { headers: corsHeaders });

  try {
    const { powerKw, pricePerKwh, durationMinutes } = await req.json();
    const durationHours = (durationMinutes || 60) / 60.0;
    const energyKwh = Number(powerKw) * durationHours;
    const electricityCost = energyKwh * Number(pricePerKwh);
    const platformFee = 12.0;
    const totalCost = electricityCost + platformFee;

    return new Response(
      JSON.stringify({
        energyKwh: Number(energyKwh.toFixed(2)),
        electricityCostInr: Number(electricityCost.toFixed(2)),
        platformFeeInr: platformFee,
        totalCostInr: Number(totalCost.toFixed(2)),
        currency: "INR"
      }),
      { headers: { ...corsHeaders, "Content-Type": "application/json" } }
    );
  } catch (err) {
    return new Response(JSON.stringify({ error: err.message }), {
      status: 400,
      headers: { ...corsHeaders, "Content-Type": "application/json" }
    });
  }
});
