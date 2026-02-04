const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

async function req(path, options = {}) {
  const res = await fetch(`${API_BASE}${path}`, {
    headers: { "Content-Type": "application/json", ...(options.headers || {}) },
    ...options
  });

  const text = await res.text();
  const data = text ? JSON.parse(text) : null;

  if (!res.ok) throw new Error(data?.error || `Request failed (${res.status})`);
  return data;
}

export const api = {
  // habits
  listHabits: () => req("/api/v1/habits"),
  createHabit: (name) =>
    req("/api/v1/habits", { method: "POST", body: JSON.stringify({ name }) }),
  setHabitActive: (id, active) =>
    req(`/api/v1/habits/${id}`, { method: "PATCH", body: JSON.stringify({ active }) }),

  // checkins
  upsertCheckIn: ({ habitId, date, status, note }) =>
    req("/api/v1/checkins", {
      method: "POST",
      body: JSON.stringify({ habitId, date, status, note })
    }),
  listCheckIns: (date) =>
    req(`/api/v1/checkins?date=${encodeURIComponent(date)}`),

  // summary
  summaryToday: () => req("/api/v1/summary/today"),

  // interview
  addInterview: (payload) =>
    req("/api/v1/interview/entries", { method: "POST", body: JSON.stringify(payload) }),
  listInterview: (topic = "") =>
    req(`/api/v1/interview/entries${topic ? `?topic=${encodeURIComponent(topic)}` : ""}`),

  // webhooks (optional for later UI)
  recentWebhookEvents: () => req("/api/v1/webhooks/recent").catch(() => []),

  // health
  live: () => req("/api/v1/health/live"),
  ready: () => req("/api/v1/health/ready")
};

const SSE_PATH = "/api/v1/stream/events";

export function streamEvents(onEvent) {
  const es = new EventSource(`${API_BASE}${SSE_PATH}`);

  es.addEventListener("activity", (e) => {
    try {
      onEvent(JSON.parse(e.data));
    } catch {
      onEvent({ type: "raw", data: e.data });
    }
  });

  // keep silent for now (browser reconnects automatically)
  es.onerror = () => {};

  return () => es.close();
}
