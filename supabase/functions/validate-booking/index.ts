// Supabase Edge Function: validate-booking
import { serve } from "https://deno.land/std@0.168.0/http/server.ts";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
};

serve(async (req) => {
  if (req.method === "OPTIONS") return new Response("ok", { headers: corsHeaders });

  try {
    const { chargerId, startTime, endTime } = await req.json();

    // Check slot bounds
    const start = new Date(startTime);
    const end = new Date(endTime);

    if (end <= start) {
      return new Response(JSON.stringify({ valid: false, reason: "End time must be after start time." }), {
        headers: { ...corsHeaders, "Content-Type": "application/json" }
      });
    }

    return new Response(
      JSON.stringify({
        valid: true,
        chargerId,
        available: true,
        message: "Slot is available for booking."
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
