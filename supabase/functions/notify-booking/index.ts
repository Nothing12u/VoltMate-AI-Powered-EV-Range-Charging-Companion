// Supabase Edge Function: notify-booking
import { serve } from "https://deno.land/std@0.168.0/http/server.ts";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
};

serve(async (req) => {
  if (req.method === "OPTIONS") return new Response("ok", { headers: corsHeaders });

  try {
    const { bookingCode, hostEmail, renterName, timeSlot } = await req.json();

    // Log notification dispatch (safely mocked for demo mode)
    console.log(`[Notification] Booking ${bookingCode} confirmed for ${renterName} at slot ${timeSlot}`);

    return new Response(
      JSON.stringify({
        delivered: true,
        bookingCode,
        channel: "PUSH_AND_EMAIL",
        timestamp: new Date().toISOString()
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
