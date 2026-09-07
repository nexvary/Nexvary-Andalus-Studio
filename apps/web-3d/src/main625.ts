import * as THREE from "three";
import "./style625.css";

type LockMap = Record<string, boolean>;
type MaterialRow = { id: string; name_ar: string; name_en: string; purchase_unit: string };

const API = (localStorage.getItem("andalus-api") || "http://127.0.0.1:8000").replace(/\/$/, "");
const locks: LockMap = {
  massing: true,
  floorCount: true,
  openings: true,
  entrance: false,
  roofline: true,
  roomBoundaries: true,
};

let materials: MaterialRow[] = [];
let lang: "ar" | "en" = "ar";

const labels = {
  ar: { title: "استوديو الأندلس", materials: "الخامات", quantity: "حساب الكمية", ai: "خطة المعماري الذكي", export: "تصدير DXF", area: "المساحة م²", status: "Stage 625 — Project Intelligence" },
  en: { title: "Andalus Studio", materials: "Materials", quantity: "Estimate quantity", ai: "AI architect plan", export: "Export DXF", area: "Area m²", status: "Stage 625 — Project Intelligence" },
};

async function apiJson(path: string, init?: RequestInit): Promise<unknown> {
  const response = await fetch(`${API}${path}`, { ...init, headers: { "Content-Type": "application/json", ...(init?.headers || {}) } });
  if (!response.ok) throw new Error(`${response.status} ${await response.text()}`);
  return response.json();
}

function render(): void {
  const t = labels[lang];
  document.documentElement.lang = lang;
  document.documentElement.dir = lang === "ar" ? "rtl" : "ltr";
  const app = document.querySelector<HTMLDivElement>("#app");
  if (!app) throw new Error("#app missing");
  app.innerHTML = `
    <div class="shell">
      <aside class="panel side">
        <h1>Nexvary Andalus Studio</h1><p>${t.title}</p>
        <button id="lang">${lang === "ar" ? "EN" : "عربي"}</button>
        <h2>Architectural Lock</h2>
        <div id="locks"></div>
      </aside>
      <main>
        <header><strong>${t.status}</strong><span id="health">API …</span></header>
        <section class="workspace">
          <div id="viewport"></div>
          <aside class="panel inspector">
            <h2>${t.materials}</h2>
            <select id="material"></select>
            <input id="area" type="number" min="0" step="0.1" value="10" aria-label="${t.area}" />
            <button id="quantity">${t.quantity}</button>
            <pre id="quantityOut">—</pre>
            <button id="ai">${t.ai}</button>
            <pre id="aiOut">—</pre>
            <button id="dxf">${t.export}</button>
          </aside>
        </section>
      </main>
    </div>`;

  const lockRoot = document.querySelector<HTMLDivElement>("#locks")!;
  lockRoot.innerHTML = Object.entries(locks).map(([key, value]) => `<label class="lock"><span>${key}</span><input data-lock="${key}" type="checkbox" ${value ? "checked" : ""}></label>`).join("");
  lockRoot.querySelectorAll<HTMLInputElement>("input[data-lock]").forEach((input) => {
    input.onchange = () => { locks[input.dataset.lock || ""] = input.checked; };
  });
  const materialSelect = document.querySelector<HTMLSelectElement>("#material")!;
  materialSelect.innerHTML = materials.map((m) => `<option value="${m.id}">${lang === "ar" ? m.name_ar : m.name_en} (${m.purchase_unit})</option>`).join("");

  document.querySelector<HTMLButtonElement>("#lang")!.onclick = () => { lang = lang === "ar" ? "en" : "ar"; render(); mountScene(); };
  document.querySelector<HTMLButtonElement>("#quantity")!.onclick = async () => {
    const area = Number(document.querySelector<HTMLInputElement>("#area")!.value);
    const result = await apiJson("/v1/quantities/surface", { method: "POST", body: JSON.stringify({ material_id: materialSelect.value, area_m2: area }) });
    document.querySelector<HTMLPreElement>("#quantityOut")!.textContent = JSON.stringify(result, null, 2);
  };
  document.querySelector<HTMLButtonElement>("#ai")!.onclick = async () => {
    const result = await apiJson("/v1/ai/plan", { method: "POST", body: JSON.stringify({ style_school: "nasrid-granada", space_type: "courtyard", source_kind: "floorplan", locks }) });
    document.querySelector<HTMLPreElement>("#aiOut")!.textContent = JSON.stringify(result, null, 2);
  };
  document.querySelector<HTMLButtonElement>("#dxf")!.onclick = async () => {
    const response = await fetch(`${API}/v1/exports/plan.dxf`, { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ units: "metric", walls: [{ x1: 0, y1: 0, x2: 8, y2: 0 }, { x1: 8, y1: 0, x2: 8, y2: 6 }, { x1: 8, y1: 6, x2: 0, y2: 6 }, { x1: 0, y1: 6, x2: 0, y2: 0 }] }) });
    if (!response.ok) throw new Error(await response.text());
    const blob = await response.blob();
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a"); link.href = url; link.download = "andalus-plan.dxf"; link.click(); URL.revokeObjectURL(url);
  };
}

async function bootstrap(): Promise<void> {
  try {
    const result = await apiJson("/v1/materials") as { materials: MaterialRow[] };
    materials = result.materials;
  } catch { materials = []; }
  render(); mountScene();
  try {
    const health = await apiJson("/health") as { version: string };
    document.querySelector<HTMLSpanElement>("#health")!.textContent = `API ${health.version}`;
  } catch { document.querySelector<HTMLSpanElement>("#health")!.textContent = "API offline"; }
}

let renderer: any;
function mountScene(): void {
  renderer?.dispose();
  const viewport = document.querySelector<HTMLDivElement>("#viewport")!;
  const scene = new THREE.Scene(); scene.background = new THREE.Color(0x071621);
  const camera = new THREE.PerspectiveCamera(48, 1, 0.1, 100); camera.position.set(9, 7, 10); camera.lookAt(0, 1, 0);
  renderer = new THREE.WebGLRenderer({ antialias: true }); renderer.setPixelRatio(Math.min(devicePixelRatio, 2)); viewport.appendChild(renderer.domElement);
  scene.add(new THREE.HemisphereLight(0xffffff, 0x16342b, 2.5));
  const stone = new THREE.MeshStandardMaterial({ color: 0xd8cfb8, roughness: 0.8 });
  const floor = new THREE.Mesh(new THREE.BoxGeometry(10, 0.15, 7), stone); scene.add(floor);
  for (const z of [-2.7, 2.7]) for (let x = -4; x <= 4; x += 1.6) { const c = new THREE.Mesh(new THREE.CylinderGeometry(0.1, 0.13, 2.5, 16), stone); c.position.set(x, 1.3, z); scene.add(c); }
  const pool = new THREE.Mesh(new THREE.BoxGeometry(4, 0.08, 1.3), new THREE.MeshStandardMaterial({ color: 0x197b83 })); pool.position.y = 0.08; scene.add(pool);
  const resize = () => { camera.aspect = viewport.clientWidth / Math.max(1, viewport.clientHeight); camera.updateProjectionMatrix(); renderer!.setSize(viewport.clientWidth, viewport.clientHeight, false); };
  new ResizeObserver(resize).observe(viewport); resize(); renderer.render(scene, camera);
}

void bootstrap();
