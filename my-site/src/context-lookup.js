const API_BASE = 'http://localhost:8080';

const form = document.getElementById('context-form');
const input = document.getElementById('context-input');
const result = document.getElementById('context-result');

form.addEventListener('submit', async (e) => {
  e.preventDefault();
  const service = input.value.trim();
  if (!service) return;

  result.hidden = false;
  result.textContent = 'Loading…';

  try {
    const res = await fetch(`${API_BASE}/context?service=${encodeURIComponent(service)}`);
    result.textContent = await res.text();
  } catch {
    result.textContent = 'Could not reach the server. Is sdd-mcp-server running on port 8080?';
  }
});
