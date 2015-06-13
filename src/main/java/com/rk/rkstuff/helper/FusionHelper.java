package com.rk.rkstuff.helper;

public class FusionHelper {

    public enum FusionCoreDir {
        //ordered clockwise
        XpZz(1, 0),
        XpZn(1, -1),
        XzZn(0, -1),
        XnZn(-1, -1),
        XnZz(-1, 0),
        XnZp(-1, 1),
        XzZp(0, 1),
        XpZp(1, 1);

        public int xOff;
        public int zOff;

        FusionCoreDir(int xOff, int zOff) {
            this.xOff = xOff;
            this.zOff = zOff;
        }

        public FusionCoreDir opposite() {
            switch (this) {
                case XpZz:
                    return XnZz;
                case XpZn:
                    return XnZp;
                case XzZn:
                    return XzZp;
                case XnZn:
                    return XpZp;
                case XnZz:
                    return XpZz;
                case XnZp:
                    return XpZn;
                case XzZp:
                    return XzZn;
                case XpZp:
                    return XnZn;
            }
            return null;
        }

        public FusionCoreDir getNext(boolean clockwise) {
            if (clockwise) {
                if (this.ordinal() == 0) return FusionCoreDir.values()[FusionCoreDir.values().length - 1];
                return FusionCoreDir.values()[this.ordinal() - 1];
            } else {
                return FusionCoreDir.values()[(this.ordinal() + 1) % FusionCoreDir.values().length];
            }
        }

        public boolean isEdge() {
            return xOff != 0 && zOff != 0;
        }

    }

    public static class FusionStructure {
        public Pos ringStart;
        public Pos ringEnd;
        public FusionCoreDir startDir;
        public int[] lengths = new int[9]; //FusionCore is always an octagon, so we have 9 side lengths (the side with the control base needs 2 lengths
        public boolean isClockwise;
        public MultiBlockHelper.Bounds controlBounds;
        public Pos master;
    }

    public static class FusionPos {
        public Pos p;
        public boolean isCore, isCase;

        public FusionPos clone() {
            FusionPos newPos = new FusionPos();
            newPos.p = this.p.clone();
            newPos.isCore = this.isCore;
            newPos.isCase = this.isCase;
            return newPos;
        }
    }

    public static boolean iterateRing(FusionStructure setup, IFusionVisitor visitor) {
        FusionCoreDir dir = setup.startDir;
        Pos start = new Pos(setup.ringStart.x, setup.ringStart.y, setup.ringStart.z);
        for (int i = 0; i < setup.lengths.length; i++) {
            FusionPos pos = new FusionPos();
            if (!dir.isEdge()) {
                if (dir.xOff != 0) {
                    for (int y = start.y - 2; y <= start.y + 2; y++) {
                        for (int z = start.z - 2; z <= start.z + 2; z++) {
                            pos.p = new Pos(start.x, y, z);
                            pos.isCore = start.z == z && start.y == y;
                            pos.isCase = !pos.isCore && Math.abs(start.y - y) != 2 && Math.abs(start.z - z) != 2;
                            if (!visitBlocks(dir, pos, setup.lengths[i], visitor)) {
                                return false;
                            }
                        }
                    }
                } else {
                    for (int x = start.x - 2; x <= start.x + 2; x++) {
                        for (int y = start.y - 2; y <= start.y + 2; y++) {
                            pos.p = new Pos(x, y, start.z);
                            pos.isCore = start.x == x && start.y == y;
                            pos.isCase = !pos.isCore && Math.abs(start.y - y) != 2 && Math.abs(start.x - x) != 2;
                            if (!visitBlocks(dir, pos, setup.lengths[i], visitor)) {
                                return false;
                            }
                        }
                    }
                }

                start.x += dir.xOff * setup.lengths[i];
                start.z += dir.zOff * setup.lengths[i];

                dir = dir.getNext(setup.isClockwise);

            } else {
                boolean swap = false;
                if ((dir.xOff == -1 && dir.zOff == -1) || (dir.xOff == 1 && dir.zOff == 1)) {
                    start.x += dir.xOff * (setup.lengths[i] - 1);
                    start.z += dir.zOff * (setup.lengths[i] - 1);
                    dir = dir.opposite();
                    swap = true;
                }

                for (int y = start.y - 2; y <= start.y + 2; y++) {
                    for (int zOff = -3; zOff <= 1; zOff++) {
                        pos.p = new Pos(start.x, y, start.z - zOff * dir.zOff);
                        pos.isCore = zOff == 0 && y == start.y;
                        pos.isCase = !pos.isCore && zOff != -3 && Math.abs(start.y - y) != 2;
                        if (!visitBlocks(dir, pos, setup.lengths[i] + zOff, visitor)) {
                            return false;
                        }
                    }

                    pos.p = new Pos(start.x + dir.xOff, y, start.z - dir.zOff);
                    pos.isCore = false;
                    pos.isCase = Math.abs(start.y - y) != 2;
                    if (!visitBlocks(dir, pos, setup.lengths[i], visitor)) {
                        return false;
                    }

                    pos.p = new Pos(start.x, y, start.z - 2 * dir.zOff);
                    pos.isCore = false;
                    pos.isCase = false;
                    if (!visitor.visit(pos)) {
                        return false;
                    }

                    pos.p = new Pos(start.x, y, start.z - 2 * dir.zOff);
                    pos.p.x += dir.xOff * (setup.lengths[i] + 1);
                    pos.p.z += dir.zOff * (setup.lengths[i] + 1);
                    pos.isCore = false;
                    pos.isCase = false;
                    if (!visitor.visit(pos)) {
                        return false;
                    }

                    pos.p = new Pos(start.x + dir.xOff, y, start.z - 2 * dir.zOff);
                    pos.isCore = false;
                    pos.isCase = false;
                    if (!visitBlocks(dir, pos, setup.lengths[i] + 1, visitor)) {
                        return false;
                    }
                }

                if (swap) {
                    start.x += dir.xOff * (setup.lengths[i] - 1);
                    start.z += dir.zOff * (setup.lengths[i] - 1);
                    dir = dir.opposite();
                }

                start.x += dir.xOff * (setup.lengths[i] - 1);
                start.z += dir.zOff * (setup.lengths[i] - 1);

                dir = dir.getNext(setup.isClockwise);

                start.x += dir.xOff;
                start.z += dir.zOff;

            }
        }
        return true;
    }

    private static boolean visitBlocks(FusionCoreDir dir, FusionPos start, int length, IFusionVisitor visitor) {
        FusionPos currentPos = start.clone();
        for (int i = 0; i < length; i++) {
            if (!visitor.visit(currentPos)) {
                return false;
            }
            currentPos.p.x += dir.xOff;
            currentPos.p.z += dir.zOff;
        }
        return true;
    }

    public static boolean iterateControl(FusionStructure setup, IFusionVisitor visitor) {
        int xMin = setup.controlBounds.getMinX();
        int yMin = setup.controlBounds.getMinY();
        int zMin = setup.controlBounds.getMinZ();
        int xMax = setup.controlBounds.getMaxX();
        int yMax = setup.controlBounds.getMaxY();
        int zMax = setup.controlBounds.getMaxZ();

        FusionPos p = new FusionPos();
        MultiBlockHelper.Bounds extendedBounds = setup.controlBounds.clone();
        extendedBounds.setMaxX(extendedBounds.getMinX() - 1);
        extendedBounds.setMaxX(extendedBounds.getMinY() - 1);
        extendedBounds.setMaxX(extendedBounds.getMinZ() - 1);
        extendedBounds.setMaxX(extendedBounds.getMaxX() + 1);
        extendedBounds.setMaxX(extendedBounds.getMaxY() + 1);
        extendedBounds.setMaxX(extendedBounds.getMaxZ() + 1);
        for (MultiBlockHelper.Bounds.BlockIterator.BoundsPos bp : extendedBounds) {
            p.p = bp;
            if (bp.x == xMin || bp.x == xMax || bp.y == yMin || bp.y == yMax || bp.z == zMin || bp.z == zMax) {
                p.isCase = false;
                p.isCore = false;
            } else if (bp.x == (xMin + 1) || bp.x == (xMax - 1) || bp.y == (yMin + 1) || bp.y == (yMax - 1) || bp.z == (zMin + 1) || bp.z == (zMax - 1)) {
                p.isCase = true;
                p.isCore = false;
            } else {
                p.isCase = false;
                p.isCore = true;
            }
            if (!visitor.visit(p)) {
                return false;
            }
        }
        return true;
    }

    public interface IFusionVisitor {

        public boolean visit(FusionPos pos);

    }
}
