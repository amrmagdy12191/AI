const btn = document.getElementById('theme-toggle');

function isDark() {
  return document.documentElement.getAttribute('data-theme') === 'dark';
}

function updateLabel() {
  btn.textContent = isDark() ? 'Light mode' : 'Dark mode';
}

btn.addEventListener('click', () => {
  const next = isDark() ? 'light' : 'dark';
  document.documentElement.setAttribute('data-theme', next);
  localStorage.setItem('theme', next);
  updateLabel();
});

updateLabel();
