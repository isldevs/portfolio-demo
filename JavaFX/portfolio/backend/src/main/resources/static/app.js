'use strict';

document.documentElement.classList.add('js');
const $ = (id) => document.getElementById(id);
const linkAllowed = (url) => {
  try { return ['https:', 'http:'].includes(new URL(url).protocol); }
  catch { return false; }
};
const el = (tag, className, text) => {
  const node = document.createElement(tag);
  if (className) node.className = className;
  if (text !== undefined) node.textContent = String(text);
  return node;
};
const addExternal = (container, url, label, className = '') => {
  if (!url || !linkAllowed(url)) return;
  const a = el('a', className, label);
  a.href = url;
  a.target = '_blank';
  a.rel = 'noopener noreferrer';
  container.append(a);
};
const animation = () => {
  if (!('IntersectionObserver' in window)) {
    document.querySelectorAll('.reveal').forEach((node) => node.classList.add('visible'));
    return;
  }
  const io = new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
      if (entry.isIntersecting) { entry.target.classList.add('visible'); io.unobserve(entry.target); }
    });
  }, { threshold: 0.08 });
  document.querySelectorAll('.reveal').forEach((node) => io.observe(node));
};

const setTheme = (theme) => {
  document.documentElement.dataset.theme = theme;
  $('theme-icon').textContent = theme === 'dark' ? '☾' : '☀';
  $('theme-toggle').setAttribute('aria-label', `Switch to ${theme === 'dark' ? 'light' : 'dark'} theme`);
  try { localStorage.setItem('portfolio-theme', theme); } catch { /* private mode */ }
};
let savedTheme = 'dark';
try { savedTheme = localStorage.getItem('portfolio-theme') || 'dark'; } catch { /* private mode */ }
setTheme(savedTheme === 'light' ? 'light' : 'dark');
$('theme-toggle').addEventListener('click', () => setTheme(document.documentElement.dataset.theme === 'dark' ? 'light' : 'dark'));
$('menu-toggle').addEventListener('click', () => {
  const open = $('nav-links').classList.toggle('open');
  $('menu-toggle').setAttribute('aria-expanded', String(open));
});
document.querySelectorAll('.nav-links a').forEach((a) => a.addEventListener('click', () => {
  $('nav-links').classList.remove('open');
  $('menu-toggle').setAttribute('aria-expanded', 'false');
}));

function renderProfile(data) {
  const p = data.profile;
  $('brand-name').textContent = p.name.toUpperCase() + '.';
  $('brand-initials').textContent = p.initials || 'YN';
  $('intro-name').textContent = p.name.toUpperCase();
  const headlineWords = p.headline.split(/\s+/);
  const accentedWords = headlineWords.splice(-2).join(' ');
  const headline = $('hero-headline');
  headline.replaceChildren(document.createTextNode(headlineWords.join(' ') + ' '), el('span', 'headline-accent', accentedWords));
  $('hero-summary').textContent = p.summary;
  $('availability').textContent = p.availability;
  $('footer-name').textContent = p.name.toUpperCase() + '.';
  $('contact-location').textContent = p.location;
  $('contact-email').textContent = p.email;
  $('contact-email').href = 'mailto:' + p.email;
  document.title = `${p.name} | ${p.role}`;
  const stats = $('stats'); stats.replaceChildren();
  (p.stats || []).forEach((item) => {
    const stat = el('div', 'stat'); stat.append(el('strong', '', item.value), el('span', '', item.label)); stats.append(stat);
  });
  const socials = $('hero-social'); socials.replaceChildren(el('span', 'hero-social-label', 'FIND ME ON'));
  (p.social || []).forEach((social) => addExternal(socials, social.url, `${social.label} ↗`));
  if (p.resumeUrl) addExternal(socials, p.resumeUrl, 'Resume ↗');
}

function renderProjects(projects) {
  const categories = ['All', ...new Set(projects.map((p) => p.category))];
  const filters = $('project-filters'); filters.replaceChildren();
  const grid = $('project-grid');
  function draw(active) {
    grid.replaceChildren();
    projects.filter((p) => active === 'All' || p.category === active).forEach((p) => {
      const card = el('article', 'project-card reveal visible');
      const top = el('div', 'project-top');
      top.append(el('span', 'project-number', `PROJECT / ${p.id}`), el('span', 'project-category', p.category));
      card.append(top, el('h3', 'project-title', p.title), el('p', 'project-description', p.description));
      const bottom = el('div', 'project-footer'); const tags = el('div', 'stack-tags');
      (p.stack || []).forEach((item) => tags.append(el('span', 'tag', item)));
      const links = el('div', 'project-links');
      addExternal(links, p.demoUrl, 'Live ↗'); addExternal(links, p.sourceUrl, 'Code ↗');
      if (!links.children.length) links.append(el('span', 'project-year', p.year));
      bottom.append(tags, links); card.append(bottom); grid.append(card);
    });
    if (!grid.children.length) grid.append(el('p', 'loading', 'No projects in this category yet.'));
    filters.querySelectorAll('button').forEach((button) => button.setAttribute('aria-pressed', String(button.dataset.category === active)));
  }
  categories.forEach((category) => {
    const button = el('button', 'filter-button', category); button.type = 'button'; button.dataset.category = category;
    button.addEventListener('click', () => draw(category)); filters.append(button);
  });
  draw('All');
}

function renderSkills(skills) {
  const grid = $('skill-grid'); grid.replaceChildren();
  const icons = ['⌘', '◈', '⬡', '✧'];
  skills.forEach((s, i) => {
    const card = el('article', 'skill-card reveal visible');
    card.append(el('div', 'skill-icon', icons[i % icons.length]), el('h3', '', s.group));
    const list = el('div', 'skill-list');
    s.items.forEach((item) => list.append(el('span', '', item)));
    card.append(list); grid.append(card);
  });
}
function renderExperience(items) {
  const container = $('experience-list'); container.replaceChildren();
  items.forEach((item) => {
    const row = el('article', 'timeline-item');
    const detail = el('div');
    detail.append(el('h3', '', item.role), el('p', 'timeline-company', item.company), el('p', 'timeline-description', item.description));
    row.append(el('div', 'timeline-period', item.period), detail); container.append(row);
  });
}

async function loadPortfolio() {
  try {
    const response = await fetch('/api/v1/portfolio', { headers: { Accept: 'application/json' } });
    if (!response.ok) throw new Error('Backend unavailable');
    const data = await response.json();
    renderProfile(data); renderProjects(data.projects || []); renderSkills(data.skills || []); renderExperience(data.experience || []);
  } catch {
    $('project-grid').replaceChildren(el('p', 'loading', 'Unable to load projects. Check that the Spring Boot server is running.'));
    $('project-filters').replaceChildren();
  }
  animation();
}

$('contact-form').addEventListener('submit', async (event) => {
  event.preventDefault();
  const form = event.currentTarget; const status = $('form-status'); const button = $('send-button');
  status.classList.remove('error'); status.textContent = '';
  if (!form.reportValidity()) return;
  const data = Object.fromEntries(new FormData(form).entries());
  if (String(data.message).trim().length < 10) { status.classList.add('error'); status.textContent = 'Please enter at least 10 characters.'; return; }
  button.disabled = true; button.textContent = 'Sending…';
  try {
    const response = await fetch('/api/v1/contact', {
      method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(data)
    });
    if (response.status === 429) throw new Error('Too many requests. Please try again later.');
    if (!response.ok) throw new Error('Could not send your message. Please check your details.');
    status.textContent = '✓ Message sent. Thank you for reaching out!'; form.reset();
  } catch (error) { status.classList.add('error'); status.textContent = error.message || 'Something went wrong.'; }
  finally { button.disabled = false; button.textContent = 'Send message ↗'; }
});
loadPortfolio();
