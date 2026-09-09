import * as Y from "yjs";

export type LockKey =
  | "massing"
  | "floorCount"
  | "openings"
  | "entrance"
  | "roofline"
  | "roomBoundaries";

export type CollaborationLocks = Record<LockKey, boolean>;

export type CollaborationDoc = {
  doc: Y.Doc;
  setLock: (key: LockKey, value: boolean) => void;
  readLocks: () => CollaborationLocks;
  encodeUpdate: () => Uint8Array;
  applyUpdate: (update: Uint8Array) => void;
  undo: () => void;
  redo: () => void;
};

const DEFAULT_LOCKS: CollaborationLocks = {
  massing: true,
  floorCount: true,
  openings: true,
  entrance: false,
  roofline: true,
  roomBoundaries: true,
};

export function createProjectCollaboration(
  initial: Partial<CollaborationLocks> = {},
): CollaborationDoc {
  const doc = new Y.Doc();
  const lockMap = doc.getMap<boolean>("architecturalLocks");
  const undoManager = new Y.UndoManager(lockMap);

  doc.transact(() => {
    const merged = { ...DEFAULT_LOCKS, ...initial };
    for (const [key, value] of Object.entries(merged)) {
      if (!lockMap.has(key)) lockMap.set(key, Boolean(value));
    }
  }, "bootstrap");

  return {
    doc,
    setLock(key, value) {
      doc.transact(() => lockMap.set(key, value), "local-ui");
    },
    readLocks() {
      const result = { ...DEFAULT_LOCKS };
      for (const key of Object.keys(result) as LockKey[]) {
        const value = lockMap.get(key);
        if (typeof value === "boolean") result[key] = value;
      }
      return result;
    },
    encodeUpdate() {
      return Y.encodeStateAsUpdate(doc);
    },
    applyUpdate(update) {
      Y.applyUpdate(doc, update, "remote");
    },
    undo() {
      undoManager.undo();
    },
    redo() {
      undoManager.redo();
    },
  };
}
