import * as THREE from "three";
import "./style.css";

type Lang = "ar" | "en";

const copy = {
  ar: {
    subtitle: "العمارة الأندلسية والذكاء التصميمي",
    project: "مشروع فناء غرناطي",
    projectMeta: "مخطط تجريبي • أمتار • حفظ تلقائي",
    design: "التصميم",
    patterns: "الزخارف",
    floorplan: "المخطط 2D",
    view3d: "المشهد 3D",
    courtyard: "الفناء",
    materials: "الخامات",
    ai: "المعماري الذكي",
    export: "تصدير",
    generate: "توليد تصميم",
    inspector: "Architectural Lock",
    inspectorText: "ثبّت العناصر التي لا تسمح للذكاء الاصطناعي بتغييرها أثناء إعادة التصميم.",
    massing: "كتلة المبنى",
    floors: "عدد الطوابق",
    openings: "الأبواب والنوافذ",
    entrance: "المدخل",
    roofline: "خط السطح",
    rooms: "حدود الغرف",
  },
  en: {
    subtitle: "Andalusian Architecture & Design Intelligence",
    project: "Granada Courtyard Project",
    projectMeta: "Concept plan • Metric • Autosaved",
    design: "Design",
    patterns: "Patterns",
    floorplan: "2D Plan",
    view3d: "3D Scene",
    courtyard: "Courtyard",
    materials: "Materials",
    ai: "AI Architect",
    export: "Export",
    generate: "Generate Design",
    inspector: "Architectural Lock",
    inspectorText: "Lock geometry the AI is not allowed to alter during redesign.",
    massing: "Building massing",
    floors: "Floor count",
    openings: "Doors & windows",
    entrance: "Entrance",
    roofline: "Roofline",
    rooms: "Room boundaries",
  },
};

let lang: Lang = "ar";

function renderShell() {
  const t = copy[lang];
  document.documentElement.lang = lang;
  document.documentElement.dir = lang === "ar" ? "rtl" : "ltr";
  const app = document.querySelector<HTMLDivElement>("#app")!;
  app.innerHTML = `
    <div class="app-shell">
      <aside class="sidebar">
        <div class="brand"><div class="logo">✦</div><div><h1>Nexvary Andalus Studio</h1><p>${t.subtitle}</p></div></div>
        <div class="section-title">${t.design}</div>
        <button class="tool active"><span>${t.view3d}</span><span>◈</span></button>
        <button class="tool"><span>${t.floorplan}</span><span>⌗</span></button>
        <button class="tool"><span>${t.courtyard}</span><span>⌂</span></button>
        <div class="section-title">${t.patterns}</div>
        <button class="tool"><span>Zellij Studio</span><span class="badge">LIVE</span></button>
        <button class="tool"><span>Rosette Lab</span><span>✺</span></button>
        <button class="tool"><span>${t.materials}</span><span>▧</span></button>
        <div class="section-title">AI</div>
        <button class="tool"><span>${t.ai}</span><span class="badge">LOCK</span></button>
      </aside>
      <main class="main">
        <header class="topbar">
          <div class="project-meta"><strong>${t.project}</strong><span>${t.projectMeta}</span></div>
          <div class="actions">
            <button class="action" id="langButton">${lang === "ar" ? "EN" : "عربي"}</button>
            <button class="action">${t.export}</button>
            <button class="action primary">${t.generate}</button>
          </div>
        </header>
        <section class="workspace">
          <div id="viewport"></div>
          <aside class="inspector">
            <h2>${t.inspector}</h2><p>${t.inspectorText}</p>
            ${lockRow(t.massing, true)}
            ${lockRow(t.floors, true)}
            ${lockRow(t.openings, true)}
            ${lockRow(t.entrance, false)}
            ${lockRow(t.roofline, true)}
            ${lockRow(t.rooms, true)}
          </aside>
          <div class="status"><span>Stage 425 Studio Core</span><span>RTL ✓</span><span>Geometry Engine ✓</span><span>3D ✓</span></div>
        </section>
      </main>
    </div>`;

  app.querySelector<HTMLButtonElement>("#langButton")!.onclick = () => {
    lang = lang === "ar" ? "en" : "ar";
    renderShell();
    mountScene();
  };
}

function lockRow(label: string, checked: boolean) {
  return `<label class="lock-row"><span>${label}</span><input class="lock" type="checkbox" ${checked ? "checked" : ""}></label>`;
}

let renderer: THREE.WebGLRenderer | undefined;

function mountScene() {
  renderer?.dispose();
  const viewport = document.querySelector<HTMLDivElement>("#viewport")!;
  const scene = new THREE.Scene();
  scene.background = new THREE.Color(0x071621);
  scene.fog = new THREE.Fog(0x071621, 18, 38);

  const camera = new THREE.PerspectiveCamera(46, 1, 0.1, 100);
  camera.position.set(9, 7.5, 11);
  camera.lookAt(0, 1.7, 0);

  renderer = new THREE.WebGLRenderer({ antialias: true });
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
  renderer.shadowMap.enabled = true;
  viewport.appendChild(renderer.domElement);

  scene.add(new THREE.HemisphereLight(0xdfefff, 0x1d2b24, 2.4));
  const key = new THREE.DirectionalLight(0xffe7b2, 3);
  key.position.set(5, 9, 4);
  key.castShadow = true;
  scene.add(key);

  const stone = new THREE.MeshStandardMaterial({ color: 0xd8cfb8, roughness: 0.8 });
  const emerald = new THREE.MeshStandardMaterial({ color: 0x1d6b59, roughness: 0.55, metalness: 0.05 });
  const water = new THREE.MeshPhysicalMaterial({ color: 0x2b8d9a, transmission: 0.15, roughness: 0.18 });

  const floor = new THREE.Mesh(new THREE.BoxGeometry(11, 0.18, 8), stone);
  floor.position.y = -0.12;
  floor.receiveShadow = true;
  scene.add(floor);

  for (const z of [-3.2, 3.2]) {
    for (let x = -4.5; x <= 4.5; x += 1.5) {
      const column = new THREE.Mesh(new THREE.CylinderGeometry(0.11, 0.14, 2.65, 20), stone);
      column.position.set(x, 1.32, z);
      column.castShadow = true;
      scene.add(column);
      const capital = new THREE.Mesh(new THREE.BoxGeometry(0.36, 0.16, 0.36), emerald);
      capital.position.set(x, 2.65, z);
      scene.add(capital);
    }
  }

  const pool = new THREE.Mesh(new THREE.BoxGeometry(4.8, 0.08, 1.45), water);
  pool.position.set(0, 0.02, 0);
  scene.add(pool);

  const fountainBase = new THREE.Mesh(new THREE.CylinderGeometry(0.65, 0.8, 0.42, 32), stone);
  fountainBase.position.set(0, 0.18, 0);
  fountainBase.castShadow = true;
  scene.add(fountainBase);

  const grid = new THREE.GridHelper(20, 20, 0x35564f, 0x1c322f);
  grid.position.y = 0.01;
  scene.add(grid);

  function resize() {
    const { clientWidth, clientHeight } = viewport;
    camera.aspect = clientWidth / Math.max(clientHeight, 1);
    camera.updateProjectionMatrix();
    renderer!.setSize(clientWidth, clientHeight, false);
  }
  const observer = new ResizeObserver(resize);
  observer.observe(viewport);
  resize();

  let angle = 0;
  const animate = () => {
    if (!renderer || !renderer.domElement.isConnected) {
      observer.disconnect();
      return;
    }
    angle += 0.0012;
    camera.position.x = Math.sin(angle) * 11;
    camera.position.z = Math.cos(angle) * 11;
    camera.lookAt(0, 1.3, 0);
    renderer.render(scene, camera);
    requestAnimationFrame(animate);
  };
  animate();
}

renderShell();
mountScene();
