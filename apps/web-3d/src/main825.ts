import * as THREE from "three";
import "./style825.css";

type LockMap = Record<"massing" | "floorCount" | "openings" | "entrance" | "roofline" | "roomBoundaries", boolean>;
type MaterialRow = { id: string; name_ar: string; name_en: string; purchase_unit: string };
type AssetRow = { asset_id: string; title: string; category: string; license_id: string; author: string | null; usableInProduct: boolean };
type AdapterRow = { adapter_id: string; display_name: string; configured: boolean; software_license: string };
type ProjectHead = { project_id: string; revision: number; fingerprint: string; updated_at: string };
type Lang = "ar" | "en";

const API = (localStorage.getItem("andalus-api") || "http://127.0.0.1:8000").replace(/\/$/, "");
const locks: LockMap = { massing: true, floorCount: true, openings: true, entrance: false, roofline: true, roomBoundaries: true };
let lang: Lang = "ar";
let materials: MaterialRow[] = [];
let assets: AssetRow[] = [];
let adapters: AdapterRow[] = [];
let currentHead: ProjectHead | null = null;
let renderer: THREE.WebGLRenderer | null = null;

const copy = {
  ar: {
    status: "Stage 825 — Production Runtime",
    project: "المشروع",
    save: "حفظ نسخة جديدة",
    load: "تحميل آخر نسخة",
    history: "سجل النسخ",
    materials: "الخامات والكميات",
    assets: "الأصول المرخّصة",
    adapters: "محركات الذكاء الاصطناعي",
    dryRun: "اختبار خطة AI",
    gltf: "تصدير glTF 3D",
    quantity: "احسب الكمية",
    area: "المساحة م²",
    apiOffline: "API غير متصل",
  },
  en: {
    status: "Stage 825 — Production Runtime",
    project: "Project",
    save: "Save new revision",
    load: "Load latest revision",
    history: "Revision history",
    materials: "Materials & quantities",
    assets: "Licensed assets",
    adapters: "AI adapters",
    dryRun: "Test AI plan",
    gltf: "Export glTF 3D",
    quantity: "Estimate quantity",
    area: "Area m²",
    apiOffline: "API offline",
  },
};

async function apiJson<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${API}${path}`, {
    ...init,
    headers: { "Content-Type": "application/json", ...(init?.headers || {}) },
  });
  if (!response.ok) throw new Error(`${response.status} ${await response.text()}`);
  return response.json() as Promise<T>;
}

function el<K extends keyof HTMLElementTagNameMap>(tag: K, className?: string): HTMLElementTagNameMap[K] {
  const node = document.createElement(tag);
  if (className) node.className = className;
  return node;
}

function lockEditor(): HTMLElement {
  const root = el("div", "lock-list");
  Object.entries(locks).forEach(([key, value]) => {
    const label = el("label", "lock-row");
    const text = el("span"); text.textContent = key;
    const input = el("input") as HTMLInputElement; input.type = "checkbox"; input.checked = value;
    input.onchange = () => { locks[key as keyof LockMap] = input.checked; };
    label.append(text, input); root.append(label);
  });
  return root;
}

function projectPayload(): Record<string, unknown> {
  const material = document.querySelector<HTMLSelectElement>("#material")?.value || null;
  return {
    version: "0.8.25",
    locale: lang,
    styleSchool: "nasrid-granada",
    locks: { ...locks },
    selectedMaterial: material,
    scenePreset: "courtyard",
    geometry: {
      massingDigest: "demo-courtyard-massing-v1",
      floorCount: 1,
      openingsDigest: "demo-openings-v1",
      entranceDigest: "demo-entrance-v1",
      rooflineDigest: "demo-roof-v1",
      roomBoundariesDigest: "demo-rooms-v1",
    },
  };
}

function render(): void {
  const t = copy[lang];
  document.documentElement.lang = lang;
  document.documentElement.dir = lang === "ar" ? "rtl" : "ltr";
  const app = document.querySelector<HTMLDivElement>("#app");
  if (!app) throw new Error("#app missing");
  app.replaceChildren();

  const shell = el("div", "stage825-shell");
  const side = el("aside", "stage825-side panel");
  const brand = el("div", "brand825");
  const brandName = el("strong"); brandName.textContent = "Nexvary Andalus Studio";
  const brandSub = el("span"); brandSub.textContent = "AI Architecture & Islamic Design";
  brand.append(brandName, brandSub);
  const langButton = el("button"); langButton.textContent = lang === "ar" ? "EN" : "عربي";
  langButton.onclick = () => { lang = lang === "ar" ? "en" : "ar"; render(); mountScene(); };
  const lockTitle = el("h2"); lockTitle.textContent = "Architectural Lock";
  side.append(brand, langButton, lockTitle, lockEditor());

  const main = el("main", "stage825-main");
  const header = el("header", "stage825-header");
  const status = el("strong"); status.textContent = t.status;
  const health = el("span"); health.id = "health"; health.textContent = "API …";
  header.append(status, health);

  const workspace = el("section", "stage825-workspace");
  const viewport = el("div"); viewport.id = "viewport";
  const inspector = el("aside", "panel stage825-inspector");

  const projectTitle = el("h2"); projectTitle.textContent = t.project;
  const projectId = el("input") as HTMLInputElement; projectId.id = "projectId"; projectId.value = "granada-courtyard"; projectId.maxLength = 120;
  const projectMeta = el("pre"); projectMeta.id = "projectMeta"; projectMeta.textContent = "—";
  const save = el("button"); save.textContent = t.save;
  const load = el("button"); load.textContent = t.load;
  const history = el("button"); history.textContent = t.history;
  inspector.append(projectTitle, projectId, save, load, history, projectMeta);

  const materialTitle = el("h2"); materialTitle.textContent = t.materials;
  const material = el("select") as HTMLSelectElement; material.id = "material";
  for (const row of materials) {
    const option = el("option") as HTMLOptionElement;
    option.value = row.id; option.textContent = `${lang === "ar" ? row.name_ar : row.name_en} (${row.purchase_unit})`;
    material.append(option);
  }
  const area = el("input") as HTMLInputElement; area.type = "number"; area.min = "0"; area.step = "0.1"; area.value = "10"; area.placeholder = t.area;
  const quantity = el("button"); quantity.textContent = t.quantity;
  const quantityOut = el("pre"); quantityOut.id = "quantityOut"; quantityOut.textContent = "—";
  inspector.append(materialTitle, material, area, quantity, quantityOut);

  const assetTitle = el("h2"); assetTitle.textContent = t.assets;
  const assetList = el("div", "asset-list");
  assets.forEach((asset) => {
    const row = el("div", "asset-row");
    const title = el("strong"); title.textContent = asset.title;
    const meta = el("span"); meta.textContent = `${asset.category} • ${asset.license_id}${asset.author ? ` • ${asset.author}` : ""}`;
    row.append(title, meta); assetList.append(row);
  });
  inspector.append(assetTitle, assetList);

  const adapterTitle = el("h2"); adapterTitle.textContent = t.adapters;
  const adapterList = el("div", "adapter-list");
  adapters.forEach((adapter) => {
    const row = el("div", "adapter-row");
    row.textContent = `${adapter.configured ? "●" : "○"} ${adapter.display_name} — ${adapter.software_license}`;
    adapterList.append(row);
  });
  const dryRun = el("button"); dryRun.textContent = t.dryRun;
  const aiOut = el("pre"); aiOut.id = "aiOut"; aiOut.textContent = "—";
  const gltf = el("button"); gltf.textContent = t.gltf;
  inspector.append(adapterTitle, adapterList, dryRun, aiOut, gltf);

  workspace.append(viewport, inspector); main.append(header, workspace); shell.append(side, main); app.append(shell);

  quantity.onclick = async () => {
    try {
      const result = await apiJson<unknown>("/v1/quantities/surface", { method: "POST", body: JSON.stringify({ material_id: material.value, area_m2: Number(area.value) }) });
      quantityOut.textContent = JSON.stringify(result, null, 2);
    } catch (error) { quantityOut.textContent = String(error); }
  };

  save.onclick = async () => {
    const nextRevision = currentHead?.project_id === projectId.value ? currentHead.revision + 1 : 1;
    const body = {
      project_id: projectId.value,
      revision: nextRevision,
      payload: projectPayload(),
      parent_fingerprint: nextRevision === 1 ? null : currentHead?.fingerprint,
      expected_parent: nextRevision === 1 ? null : currentHead?.fingerprint,
    };
    try {
      currentHead = await apiJson<ProjectHead>("/v1/projects/revisions", { method: "POST", body: JSON.stringify(body) });
      projectMeta.textContent = JSON.stringify(currentHead, null, 2);
    } catch (error) { projectMeta.textContent = String(error); }
  };

  load.onclick = async () => {
    try {
      const record = await apiJson<{ revision: number; fingerprint: string; payload: { locks?: Partial<LockMap> } }>(`/v1/projects/${encodeURIComponent(projectId.value)}`);
      currentHead = { project_id: projectId.value, revision: record.revision, fingerprint: record.fingerprint, updated_at: "" };
      Object.assign(locks, record.payload.locks || {});
      projectMeta.textContent = JSON.stringify(record, null, 2);
      render(); mountScene();
    } catch (error) { projectMeta.textContent = String(error); }
  };

  history.onclick = async () => {
    try { projectMeta.textContent = JSON.stringify(await apiJson<unknown>(`/v1/projects/${encodeURIComponent(projectId.value)}/history`), null, 2); }
    catch (error) { projectMeta.textContent = String(error); }
  };

  dryRun.onclick = async () => {
    try {
      const result = await apiJson<unknown>("/v1/ai/jobs/dry-run", { method: "POST", body: JSON.stringify({ style_school: "nasrid-granada", space_type: "courtyard", source_kind: "floorplan", locks, user_request: "Refine the courtyard while preserving locked geometry", idempotency_key: `${projectId.value}-${Date.now()}` }) });
      aiOut.textContent = JSON.stringify(result, null, 2);
    } catch (error) { aiOut.textContent = String(error); }
  };

  gltf.onclick = async () => {
    try {
      const walls = [{x1:0,z1:0,x2:8,z2:0},{x1:8,z1:0,x2:8,z2:6},{x1:8,z1:6,x2:0,z2:6},{x1:0,z1:6,x2:0,z2:0}];
      const response = await fetch(`${API}/v1/exports/scene.gltf`, { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ walls, project_fingerprint: currentHead?.fingerprint || null }) });
      if (!response.ok) throw new Error(await response.text());
      const blob = await response.blob(); const url = URL.createObjectURL(blob); const link = el("a") as HTMLAnchorElement;
      link.href = url; link.download = `${projectId.value || "andalus-scene"}.gltf`; link.click(); URL.revokeObjectURL(url);
    } catch (error) { aiOut.textContent = String(error); }
  };
}

async function bootstrap(): Promise<void> {
  const [materialResult, assetResult, adapterResult] = await Promise.allSettled([
    apiJson<{ materials: MaterialRow[] }>("/v1/materials"),
    apiJson<{ assets: AssetRow[] }>("/v1/assets"),
    apiJson<{ adapters: AdapterRow[] }>("/v1/ai/adapters"),
  ]);
  if (materialResult.status === "fulfilled") materials = materialResult.value.materials;
  if (assetResult.status === "fulfilled") assets = assetResult.value.assets;
  if (adapterResult.status === "fulfilled") adapters = adapterResult.value.adapters;
  render(); mountScene();
  try {
    const health = await apiJson<{ version: string }>("/health");
    const node = document.querySelector<HTMLSpanElement>("#health"); if (node) node.textContent = `API ${health.version}`;
  } catch {
    const node = document.querySelector<HTMLSpanElement>("#health"); if (node) node.textContent = copy[lang].apiOffline;
  }
}

function mountScene(): void {
  renderer?.dispose();
  const viewport = document.querySelector<HTMLDivElement>("#viewport"); if (!viewport) return;
  viewport.replaceChildren();
  const scene = new THREE.Scene(); scene.background = new THREE.Color(0x071621);
  const camera = new THREE.PerspectiveCamera(48, 1, 0.1, 100); camera.position.set(9,7,10); camera.lookAt(0,1,0);
  renderer = new THREE.WebGLRenderer({antialias:true}); renderer.setPixelRatio(Math.min(devicePixelRatio,2)); viewport.append(renderer.domElement);
  scene.add(new THREE.HemisphereLight(0xffffff,0x16342b,2.5));
  const stone = new THREE.MeshStandardMaterial({color:0xd8cfb8,roughness:0.82});
  const floor = new THREE.Mesh(new THREE.BoxGeometry(10,0.15,7),stone); scene.add(floor);
  for(const z of [-2.7,2.7]) for(let x=-4;x<=4;x+=1.6){const c=new THREE.Mesh(new THREE.CylinderGeometry(0.1,0.13,2.5,16),stone);c.position.set(x,1.3,z);scene.add(c);}
  const pool = new THREE.Mesh(new THREE.BoxGeometry(4,0.08,1.3),new THREE.MeshStandardMaterial({color:0x197b83,roughness:0.25})); pool.position.y=0.08; scene.add(pool);
  const resize=()=>{if(!renderer)return;camera.aspect=viewport.clientWidth/Math.max(1,viewport.clientHeight);camera.updateProjectionMatrix();renderer.setSize(viewport.clientWidth,viewport.clientHeight,false);renderer.render(scene,camera);};
  const observer=new ResizeObserver(resize);observer.observe(viewport);resize();
}

void bootstrap();
