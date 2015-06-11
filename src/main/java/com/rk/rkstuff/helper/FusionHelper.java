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
                return FusionCoreDir.values()[this.ordinal() + 1 % FusionCoreDir.values().length];
            } else {
                if (this.ordinal() == 0) return FusionCoreDir.values()[FusionCoreDir.values().length - 1];
                return FusionCoreDir.values()[this.ordinal() - 1];
            }
        }

        public boolean isEdge() {
            return xOff != 0 && zOff != 0;
        }

    }

    public static class FusionCoreSetup {
        public Pos src;
        public Pos end;
        public FusionCoreDir startDir;
        public int[] lengths = new int[9]; //FusionCore is always an octagon, so we have 9 side lengths (the side with the control base needs 2 lengths
        public boolean isClockwise;
    }

    public static class FusionRingPos {
        public Pos p;
        public boolean isCore, isCase;

        public FusionRingPos clone() {
            FusionRingPos newPos = new FusionRingPos();
            newPos.p = this.p.clone();
            newPos.isCore = this.isCore;
            newPos.isCase = this.isCase;
            return newPos;
        }
    }

    public static boolean iterate(FusionCoreSetup setup, IFusionRingVisitor visitor) {
        FusionCoreDir dir = setup.startDir;
        Pos p = new Pos(setup.src.x, setup.src.y, setup.src.z);
        for (int i = 0; i < FusionCoreDir.values().length; i++) {
            FusionRingPos start = new FusionRingPos();
            if (!dir.isEdge()) {
                if (dir.xOff != 0) {
                    for (int y = p.y - 2; y <= p.y + 2; y++) {
                        for (int z = p.z - 2; z <= p.z + 2; z++) {
                            start.p = new Pos(p.x, y, z);
                            start.isCore = start.p.equals(p);
                            start.isCase = Math.abs(p.y - y) == 1 || Math.abs(p.z - z) == 1;
                            if (!visitBlocks(dir, start, setup.lengths[i], visitor)) {
                                return false;
                            }
                        }
                    }
                } else {
                    for (int x = p.x - 2; x <= p.z + 2; x++) {
                        for (int y = p.y - 2; y <= p.y + 2; y++) {
                            start.p = new Pos(x, y, p.z);
                            start.isCore = start.p.equals(p);
                            start.isCase = !start.isCore && !(Math.abs(p.y - y) == 2 || Math.abs(p.x - x) == 2);
                            if (!visitBlocks(dir, start, setup.lengths[i], visitor)) {
                                return false;
                            }
                        }
                    }
                }
            } else {

                p.x += dir.xOff * (setup.lengths[i] - 1);
                p.z += dir.zOff * (setup.lengths[i] - 1);
                dir = dir.opposite();

                for (int y = p.y - 2; y <= p.y + 2; y++) {
                    for (int zOff = -3; zOff <= 1; zOff++) {
                        start.p = new Pos(p.x, y, p.z - zOff * dir.zOff);
                        start.isCore = zOff == 0 && y == p.y;
                        start.isCase = !start.isCore && zOff != -3;
                        if (!visitBlocks(dir, start, setup.lengths[i] + zOff, visitor)) {
                            return false;
                        }
                    }

                    start.p = new Pos(p.x + dir.xOff, y, p.z - dir.zOff);
                    start.isCore = false;
                    start.isCase = true;
                    if (!visitBlocks(dir, start, setup.lengths[i], visitor)) {
                        return false;
                    }

                    start.p = new Pos(p.x, y, p.z - 2 * dir.zOff);
                    start.isCore = false;
                    start.isCase = false;
                    if (!visitor.visit(start)) {
                        return false;
                    }

                    start.p = new Pos(p.x, y, p.z - 2 * dir.zOff);
                    start.p.x += dir.xOff * (setup.lengths[i] + 2);
                    start.p.z += dir.zOff * (setup.lengths[i] + 2);
                    start.isCore = false;
                    start.isCase = false;
                    if (!visitor.visit(start)) {
                        return false;
                    }

                    start.p = new Pos(p.x + dir.xOff, y, p.z - 2 * dir.zOff);
                    start.isCore = false;
                    start.isCase = false;
                    if (!visitBlocks(dir, start, setup.lengths[i] + 1, visitor)) {
                        return false;
                    }
                }
            }

            dir.opposite();

            p.x += dir.xOff * (setup.lengths[i] - 1);
            p.z += dir.zOff * (setup.lengths[i] - 1);

            dir = dir.getNext(setup.isClockwise);

            p.x += dir.xOff;
            p.z += dir.zOff;
        }

        return true;
    }

    private static boolean visitBlocks(FusionCoreDir dir, FusionRingPos start, int length, IFusionRingVisitor visitor) {
        FusionRingPos currentPos = start.clone();
        for (int i = 0; i < length; i++) {
            if (!visitor.visit(start)) {
                return false;
            }
            currentPos.p.x += dir.xOff;
            currentPos.p.z += dir.zOff;
        }
        return true;
    }

    public interface IFusionRingVisitor {

        public boolean visit(FusionRingPos pos);

    }
}
