//details of github
const GHUSER = 'rarbin';
const GHREPO = 'projectportfolio26';
const GHALL = `${GHUSER

  }/${GHREPO}`;
//save to cache/ alt TODO save to file serverside and load from
const GHCACHEDUR = 30 * 60 * 1000;

//theoretically data can be saved/transferred, for now manual
//TODO: move to its own file
const DATA = {
  git: [],
  commits: [],
  systemFile: {
    title: 'SYSTEM',
    rows: [
      ['Operating System', 'Arch Linux x86_64', false],
      ['Host', 'Surface Pro 7', false],
      ['CPU', 'Intel i5-1035G4 8 @ 3.7GHz', false],
      ['GPU', 'Intel Iris Plus Graphics G4', false],
    ]
  },
  files: {
    'TODO.txt': [
      'TODO:',
      '  - add guestbook functionality',
      '  - add guest counter',
      '  - hovering on repo files should preview pdfs/browser runnable files',
      ''
    ].join('\n'),
    'motd': [
      '=====================================',
      ' WELCOME :-)',
      '====================================='
    ].join('\n')
  },

  tools: [
    {
      name: 'IntelliJ', category: 'IDE', version: '2026.2.3', file: {
        title: 'INTELLIJ IDEA', rows: [
          ['Vendor', 'JetBrains', false], ['Edition', 'Ultimate', false], ['Platform', 'JVM, Linux', false],
          ['Language', 'Java', false], ['Uses', 'Developing front/back-end java applications', false], ['Plugins', 'Git Toolbox, JavaFX', false]]
      }
    },
    {
      name: 'PyCharm', category: 'IDE', version: '2026.2.3', file: {
        title: 'PYCHARM', rows: [
          ['Vendor', 'JetBrains', false], ['Edition', 'Ultimate', false], ['Platform', 'Python/JVM, Linux', false],
          ['Language', 'Python', false], ['Uses', 'Developing automated python scripts.', false], ['Plugins', 'Git Toolbox', false]]
      }
    },
    {
      name: 'VS Code', category: 'EDITOR', version: '1.139.1', file: {
        title: 'VS CODE', rows: [
          ['Vendor', 'Microsoft', false], ['Platform', 'Wayland, Linux', false], ['Language', 'LaTeX editor, C/C++', false],
          ['Plugins', 'LaTeX', false], ['Uses', 'Write LaTeX reports, occasional C/C++ programming.', false]]
      }
    },

    {
      name: 'Kali', category: 'VIRTUAL MACHINE', version: '2026.2', file: {
        title: 'KALI LINUX', rows: [
          ['Vendor', 'Offensive Security', false], ['Platform', 'Debian [VMWare/Qemu]', false],
          ['Tools', 'nmap, metasploit, netcat', false], ['Uses', 'Network Pen-testing & digital forensics', false]]
      }
    },
    {
      name: 'Autopsy/TSK', category: 'FILESYSTEM FORENSICS', version: '4.21', file: {
        title: 'AUTOPSY / THE SLEUTH KIT', rows: [
          ['Vendor', 'Basis Technology', false], ['Platform', 'Windows/Linux', false],
          ['File Systems', 'NTFS, ext4, HFS+, FAT', false], ['Tools', 'Hex editor, hashes, timeline, keyword search', false], ['Uses', 'Recover corrupted/deleted data on a file system using a variety of tools.', false]]
      }
    },
    {
      name: 'Cisco Packet Tracer', category: 'NETWORK SIM', version: '8.2.2', file: {
        title: 'CISCO PACKET TRACER', rows: [
          ['Vendor', 'Cisco Systems', false], ['Platform', 'Windows 10', false],
          ['Devices', 'Ip Addresses, Routers, switches, APs', false], ['Uses', 'Simulated network configuration', false]]
      }
    },
  ],

  //TODO add direct hyperlink, fix weird scaling
  projects: [
    {
      id: '001', date: '2025-10-7', title: 'CVE-2026-3544 Report/PoC', desc: 'Chrome-based Heap Buffer Overflow analysis report, with exploit PoC',
      cmd: "demo.html", tags: ['Buffer Overflow', 'Chrome'], status: 'WIP'
    },
    {
      id: '002', date: '2026-10-6', title: 'Python AI Detector', desc: 'Script that flags for common patterns in AI LLM generative texts.',
      cmd: "./detectdemo", tags: ['AI', 'Python'], status: 'DONE'
    },
    {
      id: '003', date: '2025-10-18', title: 'Java Inventory Manager', desc: 'Log the inventory of a store to its shelves, aisles and floors. Visualised on interactive map.',
      cmd: "./main.java", tags: ['Java', 'Data Structures/Algorithms'], status: 'DONE'
    },
    {
      id: '004', date: '2025-12-14', title: 'Java Election System', desc: 'Track the voter turnout/demographics of a given election',
      cmd: "./main.java", tags: ['Java', 'Data Structures/Algorithms'], status: 'DONE'
    }

  ]
};


//render page settings
const esc = s => String(s).replace(/[&<>]/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;' }[c]));

function renderProjects() {
  document.getElementById('projectList').innerHTML = DATA.projects.map(p => `
    <fieldset class="card">
      <legend>[PROJ_${p.id}] · ${p.date}</legend>
      <div class="card-title">${esc(p.title)}</div>
      <div class="card-desc">${esc(p.desc)}</div>
      <div class="card-cmd">${esc(p.cmd).replace(/\n/g, '<br>')}</div>
      <div class="card-foot">
        <div>${p.tags.map(t => `<span class="tag">${esc(t)}</span>`).join('')}</div>
        <span class="status">● ${p.status}</span>
      </div>
    </fieldset>
  `).join('');
}
//scrolly
function renderTicker(stale = false) {
  const ticker = document.getElementById('tickerInner');
  if (!ticker) return;
  //when gitapi timesout/exceeds limit
  const prefix = stale
    ? `<span><span class="hash">OFFLINE</span> cached data — </span>`
    : '';

  const span = ([hash, repo, msg, time]) =>
    `<span><span class="hash">${esc(hash)}</span> ` +
    `${esc(repo)}${esc(msg)} ` +
    `<span class="time">(${esc(time)})</span></span>`;

  const onePass = DATA.commits.map(span).join('');
  ticker.innerHTML = prefix + onePass + onePass;
}

//fetch from api
async function ghFetch(url) {
  const res = await fetch(url, {
    headers: { 'Accept': 'application/vnd.github+json' }
  });
  if (!res.ok) throw new Error(`GitHub ${res.status}`);
  return res.json();
}

function timeAgo(date) {
  const s = Math.floor((Date.now() - date.getTime()) / 1000);
  if (s < 60) return s + 's';
  if (s < 3600) return Math.floor(s / 60) + 'm';
  if (s < 86400) return Math.floor(s / 3600) + 'h';
  return Math.floor(s / 86400) + 'd';
}
//write to website cache, iffy about this approach and will probably switch to full serverside
function writeCache(key, data) {
  try {
    const payload = JSON.stringify({ at: Date.now(), data });
    localStorage.setItem(`gh_fresh_${key}`, payload);
    localStorage.setItem(`gh_stale_${key}`, payload);
  } catch (e) {
    console.warn('cache write failed:', e);
  }
}

function readFresh(key) {
  try {
    const raw = localStorage.getItem(`gh_fresh_${key}`);
    if (!raw) return null;
    const obj = JSON.parse(raw);
    if (Date.now() - obj.at > GHCACHEDUR) return null;
    return obj.data;
  } catch { return null; }
}

function readStale(key) {
  try {
    const raw = localStorage.getItem(`gh_stale_${key}`);
    if (!raw) return null;
    const obj = JSON.parse(raw);
    return { data: obj.data, at: obj.at };
  } catch { return null; }
}
//load data
async function loadGitHubData(force = false) {
  let contents, events;
  let usedStale = false;
  //display the repo content
  if (!force) {
    contents = readFresh('contents');
  }
  if (!contents) {
    try {
      contents = await ghFetch(
        `https://api.github.com/repos/${GHUSER

        }/${GHREPO}/contents/`
      );
      writeCache('contents', contents);
    } catch (err) {
      console.warn('contents fetch failed:', err.message);
      const stale = readStale('contents');
      if (stale) {
        contents = stale.data;
        usedStale = true;
        console.info(`using contents from ${new Date(stale.at).toLocaleString()}`);
      }
    }
  }

  if (Array.isArray(contents) && contents.length) {

    //structure the same way a repo does
    DATA.git = contents.map(item => ({
      repo: item.name + (item.type === 'dir' ? '/' : ''),
      stars: item.type === 'dir' ? 'DIR' : 'FILE',   //its not for stars rn but cope
      forks: null,
      issues: null,
      commits: null,
      watchers: null,
      lang: item.type === 'dir' ? 'folder' : 'file',
      size: item.size ? (item.size / 1024).toFixed(1) + ' KB' : 'N/A',
      updated: 'N/A',
      url: item.html_url,
      sha: item.sha
    }));
  } else if (!DATA.git.length) {
    DATA.git = [{
      repo: 'github/unavailable', stars: 0, forks: 0, issues: 0,
      commits: 0, watchers: 0, lang: 'N/A', size: 'N/A', updated: 'N/A'
    }];
  }
  initGitList();
  //commit history
  if (!force) {
    events = readFresh('commits');
  }
  if (!events) {
    try {
      events = await ghFetch(
        `https://api.github.com/repos/${GHUSER

        }/${GHREPO}/commits?per_page=30`
      );
      writeCache('commits', events);
    } catch (err) {
      console.warn('commits fetch failed:', err.message);
      const stale = readStale('commits');
      if (stale) {
        events = stale.data;
        usedStale = true;
        console.info(`using cached commits from ${new Date(stale.at).toLocaleString()}`);
      }
    }
  }

  const commits = (events || [])
    .map(c => ([
      'code',
      (c.sha || '').slice(0, 7) + ' | ',
      (c.commit && c.commit.message ? c.commit.message.split('\n')[0] : '').slice(0, 60),
      c.commit && c.commit.author && c.commit.author.date
        ? timeAgo(new Date(c.commit.author.date))
        : ''
    ]))
    .slice(0, 12);

  if (commits.length) {
    DATA.commits = commits;
  } else if (!DATA.commits.length) {
    DATA.commits = [['MOTD:', 'no recent commits', '', '']];
  }
  renderTicker(usedStale);

  if (usedStale) {
    console.info('GitHub unavailable .');
  }
}

function renderRepoBreakdown(repo) {
  const el = document.getElementById('ascii3d');
  const label = document.getElementById('statRepoName');

  if (!repo) {
    if (label) label.textContent = 'SELECT A REPO';
    el.textContent =
      '\n  [ nothing selected ]\n\n' +
      '  > click an item on the right\n' +
      '    to view its details\n';
    return;
  }

  if (label) label.textContent = repo.repo;

  // folder summary and file size, can go more in depth but not today 
  const lines = [];
  lines.push('  ' + repo.repo);
  lines.push('  ' + '>'.repeat(repo.repo.length));
  lines.push('  TYPE: ' + (repo.lang || '>').toUpperCase());
  lines.push('  SIZE: ' + (repo.size || '>'));
  if (repo.url) {
    lines.push('');
    lines.push('  > ' + repo.url);
  }
  lines.push('');
  lines.push('  Browsable on GitHub');

  el.textContent = lines.join('\n');
}

function initGitList() {
  const container = document.getElementById('gitList');
  if (!container) return;

  let selectedIdx = -1;

  function draw() {
    if (!DATA.git.length) {
      container.innerHTML = `<div class="git-row"><span class="repo">loading…</span></div>`;
      return;
    }

    container.innerHTML = DATA.git.map((r, i) => `
      <div class="git-row${i === selectedIdx ? ' active' : ''}" data-idx="${i}">
        <span class="repo">${esc(r.repo)}</span>
        <span class="stars">${esc(String(r.stars))}</span>
      </div>
    `).join('');

    container.querySelectorAll('.git-row').forEach(row => {
      row.addEventListener('click', () => {
        selectedIdx = Number(row.dataset.idx);
        draw();
        renderRepoBreakdown(DATA.git[selectedIdx]);
      });
    });
  }

  draw();
  renderRepoBreakdown(null);
}

//TODO remove true/false red labelling, don't really need it
function renderDataFile(file) {
  const title = document.getElementById('dataTitle');
  const list = document.getElementById('dataList');
  if (!title || !list) return;

  title.textContent = file.title;
  list.innerHTML = file.rows.map(([label, value, isRed]) => `
    <div class="data-row">
      <span class="label">${esc(label)}</span>
      <span class="value${isRed ? ' red' : ''}">${esc(value)}</span>
    </div>
  `).join('');
}


function initToolScroller() {
  const scroller = document.getElementById('toolScroller');
  const nameEl = document.getElementById('toolSelected');
  const catEl = document.getElementById('toolCategory');
  const verEl = document.getElementById('toolVersion');
  if (!scroller) return;

  let selectedIdx = -1;

  function draw() {
    scroller.innerHTML = DATA.tools.map((tool, i) => `
      <div class="tool-tile${i === selectedIdx ? ' active' : ''}" data-idx="${i}">
        ${esc(tool.name)}
      </div>
    `).join('');

    scroller.querySelectorAll('.tool-tile').forEach(tile => {
      tile.addEventListener('click', () => {
        selectedIdx = Number(tile.dataset.idx);
        const tool = DATA.tools[selectedIdx];
        draw();
        nameEl.textContent = tool.name;
        catEl.textContent = tool.category;
        verEl.textContent = tool.version;
        renderDataFile(tool.file);
      });
    });
  }

  draw();
  renderDataFile(DATA.systemFile);
}

//cowsay why am i so janky
function initTerminal() {
  const body = document.getElementById('terminalBody');
  const input = document.getElementById('terminalInput');
  const history = [];
  let hIdx = -1;

  const COW = [
    '        \\   ^__^',
    '         \\  (oo)\\_______',
    '            (__)\\       )\\/\\',
    '                ||----w |',
    '                ||     ||'
  ].join('\n');

  function balloon(text) {
    const lines = text.split('\n');
    const width = Math.max(...lines.map(l => l.length));
    const top = ' ' + '_'.repeat(width + 2);
    const bottom = ' ' + '-'.repeat(width + 2);
    const body = lines.map((l, i) => {
      const pad = l.padEnd(width, ' ');
      let left, right;
      if (lines.length === 1) { left = '<'; right = '>'; }
      else if (i === 0) { left = '/'; right = '\\'; }
      else if (i === lines.length - 1) { left = '\\'; right = '/'; }
      else { left = '|'; right = '|'; }
      return `${left} ${pad} ${right}`;
    });
    return [top, ...body, bottom].join('\n');
  }

  const CMD = {
    help: () => 'Commands: help, ls, cat, whoami, date, clear, cowsay, git, projects, tools, history, refresh',
    ls: () => 'TODO.txt  motd',
    cat: args => {
      if (!args.length) return 'usage: cat <filename>';
      return args.map(f => {
        if (Object.prototype.hasOwnProperty.call(DATA.files, f)) return DATA.files[f];
        return `cat: ${f}: No such file or directory`;
      }).join('\n');
    },
    whoami: () => 'tell me on the guestbook :-)',
    date: () => new Date().toString(),
    clear: () => { body.innerHTML = ''; return ''; },
    cowsay: args => {
      const msg = args.length ? args.join(' ') : 'welcome to the site';
      return balloon(msg) + '\n' + COW;
    },
    git: () => 'GIT REPO:\n  ' + GHALL + '\n\nCONTENTS:\n' + DATA.git.map(r => `  ${r.repo.padEnd(40)} ${r.lang.toUpperCase()}`).join('\n'),
    projects: () => 'PROJECTS:\n' + DATA.projects.map(p => `  [PROJ_${p.id}] ${p.title.padEnd(28)} - ${p.status}`).join('\n'),
    tools: () => 'TOOLS:\n' + DATA.tools.map(a => `  ${a.name.padEnd(20)} ${a.category.padEnd(22)} v${a.version}`).join('\n'),
    refresh: () => { loadGitHubData(true); return 'refreshing GitHub data…'; },
    history: () => history.length
      ? history.map((c, i) => `  ${i + 1}  ${c}`).join('\n')
      : 'No commands in history.'
  };

  function addLine(prompt, text, cls) {
    const div = document.createElement('div');
    div.className = 'line';
    if (prompt) {
      const p = document.createElement('span');
      p.className = 'prompt';
      p.textContent = prompt + ' ';
      div.appendChild(p);
    }
    const t = document.createElement('span');
    if (cls) t.className = cls;
    t.textContent = text;
    div.appendChild(t);
    body.appendChild(div);
    body.scrollTop = body.scrollHeight;
  }

  function run(line) {
    const [cmd, ...args] = line.trim().split(/\s+/);
    if (!cmd) return;
    history.push(line);
    hIdx = history.length;
    addLine('guest@rob:~#', line, 'cmd');

    if (CMD[cmd.toLowerCase()]) {
      const out = CMD[cmd.toLowerCase()](args);
      if (out) out.split('\n').forEach(l => addLine(null, l, 'output'));
    } else {
      addLine(null, `bash: ${cmd}: command not found`, 'error');
    }
  }

  input.addEventListener('keydown', e => {
    if (e.key === 'Enter') {
      const v = input.value;
      input.value = '';
      run(v);
    } else if (e.key === 'ArrowUp') {
      e.preventDefault();
      if (hIdx > 0) { hIdx--; input.value = history[hIdx]; }
    } else if (e.key === 'ArrowDown') {
      e.preventDefault();
      if (hIdx < history.length - 1) { hIdx++; input.value = history[hIdx]; }
      else { hIdx = history.length; input.value = ''; }
    }
  });

  document.querySelector('.term-in').parentElement.addEventListener('click', () => input.focus());

  ['help', 'cowsay welcome to the site'].forEach(c => {
    addLine('guest@rob:~#', c, 'cmd');
    const [cmd, ...args] = c.trim().split(/\s+/);
    CMD[cmd.toLowerCase()](args).split('\n').forEach(l => addLine(null, l, 'output'));
  });
}

//setup loader
document.addEventListener('DOMContentLoaded', () => {
  renderProjects();
  initTerminal();
  initGitList();
  renderTicker();
  initToolScroller();

  loadGitHubData();
  setInterval(() => loadGitHubData(), 30 * 60 * 1000);
});