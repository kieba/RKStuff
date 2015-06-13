package com.rk.rkstuff.helper;

import com.rk.rkstuff.RkStuff;
import net.minecraft.block.Block;

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
        public int bevelMeta;
        public Block bevelBlock;
        public boolean isBevelBlock;

        public FusionPos clone() {
            FusionPos newPos = new FusionPos();
            newPos.p = this.p.clone();
            newPos.isCore = this.isCore;
            newPos.isCase = this.isCase;
            newPos.isBevelBlock = this.isBevelBlock;
            newPos.bevelMeta = this.bevelMeta;
            newPos.bevelBlock = this.bevelBlock;
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
                            int zOff = start.z - z;
                            int yOff = start.y - y;
                            pos.p = new Pos(start.x, y, z);
                            pos.isCore = zOff == 0 && yOff == 0;
                            pos.isCase = !pos.isCore && Math.abs(yOff) != 2 && Math.abs(zOff) != 2;
                            pos.isBevelBlock = pos.isCase && yOff != 0 && zOff != 0;
                            if (pos.isBevelBlock) {
                                pos.bevelBlock = RkStuff.blockFusionCaseBevelLarge;
                                pos.bevelMeta = (zOff == 1) ? 1 : 0;
                                pos.bevelMeta |= (yOff == 1) ? 0x04 : 0;
                            }
                            if (!visitBlocks(dir, pos, setup.lengths[i], visitor)) {
                                return false;
                            }
                        }
                    }
                } else {
                    for (int x = start.x - 2; x <= start.x + 2; x++) {
                        for (int y = start.y - 2; y <= start.y + 2; y++) {
                            int xOff = start.x - x;
                            int yOff = start.y - y;
                            pos.p = new Pos(x, y, start.z);
                            pos.isCore = xOff == 0 && yOff == 0;
                            pos.isCase = !pos.isCore && Math.abs(yOff) != 2 && Math.abs(xOff) != 2;
                            pos.isBevelBlock = pos.isCase && yOff != 0 && xOff != 0;
                            if (pos.isBevelBlock) {
                                pos.bevelBlock = RkStuff.blockFusionCaseBevelLarge;
                                pos.bevelMeta = (xOff == 1) ? 2 : 3;
                                pos.bevelMeta |= (yOff == 1) ? 0x04 : 0;
                            }
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
                pos.isBevelBlock = false;
                if ((dir.xOff == -1 && dir.zOff == -1) || (dir.xOff == 1 && dir.zOff == 1)) {
                    start.x += dir.xOff * (setup.lengths[i] - 1);
                    start.z += dir.zOff * (setup.lengths[i] - 1);
                    dir = dir.opposite();
                    swap = true;
                }

                Block first = null;
                int metaFirst = 0;
                Block last = null;
                int metaLast = 0;

                for (int y = start.y - 2; y <= start.y + 2; y++) {
                    int yOff = start.y - y;
                    for (int zOff = -3; zOff <= 1; zOff++) {
                        first = null;
                        pos.p = new Pos(start.x, y, start.z - zOff * dir.zOff);
                        pos.isCore = zOff == 0 && yOff == 0;
                        pos.isCase = !pos.isCore && zOff != -3 && Math.abs(yOff) != 2;
                        pos.isBevelBlock = pos.isCase && Math.abs(yOff) <= 1 && (zOff == -2 || zOff == -1 || zOff == 1);
                        if (pos.isBevelBlock) {
                            if (yOff == 0 && zOff == -2) {
                                pos.bevelBlock = RkStuff.blockFusionCaseBevelLarge;
                                if (dir.xOff == 1 && dir.zOff == 1) {
                                    pos.bevelMeta = 0;
                                } else if (dir.xOff == 1 && dir.zOff == -1) {
                                    pos.bevelMeta = 2;
                                } else if (dir.xOff == -1 && dir.zOff == 1) {
                                    pos.bevelMeta = 3;
                                } else if (dir.xOff == -1 && dir.zOff == -1) {
                                    pos.bevelMeta = 1;
                                }
                                pos.bevelMeta |= 0x08;
                            } else if (zOff == -1 && yOff != 0) {
                                pos.bevelBlock = RkStuff.blockFusionCaseBevelSmallInverted;
                                pos.bevelMeta = (dir.xOff == 1) ? 0x01 : 0;
                                pos.bevelMeta |= (yOff == 1) ? 0x02 : 0;
                                pos.bevelMeta |= (dir.zOff == -1) ? 0x04 : 0;
                            } else if (zOff == -2) {
                                pos.bevelBlock = RkStuff.blockFusionCaseBevelSmall;
                                pos.bevelMeta = (dir.xOff == 1) ? 0x01 : 0;
                                pos.bevelMeta |= (yOff == 1) ? 0x02 : 0;
                                pos.bevelMeta |= (dir.zOff == -1) ? 0x04 : 0;
                            } else if (yOff != 0 && zOff == 1) {
                                pos.bevelBlock = RkStuff.blockFusionCaseBevelSmallInverted;
                                pos.bevelMeta = (dir.xOff == -1) ? 0x01 : 0;
                                pos.bevelMeta |= (yOff == 1) ? 0x02 : 0;
                                pos.bevelMeta |= (dir.zOff == 1) ? 0x04 : 0;

                                first = RkStuff.blockFusionCaseBevelLarge;
                                last = RkStuff.blockFusionCaseBevelLarge;

                                FusionCoreDir dirFirst = dir.getNext(!swap);
                                FusionCoreDir dirLast = dir.getNext(swap);
                                if (swap) {
                                    dirFirst = dirFirst.opposite();
                                    dirLast = dirLast.opposite();
                                }
                                if (dirFirst.xOff == 1 && dirFirst.zOff == 0) {
                                    metaFirst = 0;
                                } else if (dirFirst.xOff == -1 && dirFirst.zOff == 0) {
                                    metaFirst = 1;
                                } else if (dirFirst.xOff == 0 && dirFirst.zOff == 1) {
                                    metaFirst = 2;
                                } else if (dirFirst.xOff == 0 && dirFirst.zOff == -1) {
                                    metaFirst = 3;
                                }
                                metaFirst |= (yOff == 1) ? 0x04 : 0;

                                if (dirLast.xOff == 1 && dirLast.zOff == 0) {
                                    metaLast = 0;
                                } else if (dirLast.xOff == -1 && dirLast.zOff == 0) {
                                    metaLast = 1;
                                } else if (dirLast.xOff == 0 && dirLast.zOff == 1) {
                                    metaLast = 2;
                                } else if (dirLast.xOff == 0 && dirLast.zOff == -1) {
                                    metaLast = 3;
                                }
                                metaLast |= (yOff == 1) ? 0x04 : 0;
                            } else {
                                pos.isBevelBlock = false;
                                pos.bevelBlock = RkStuff.blockFusionCase;
                                pos.bevelMeta = 0;
                            }
                        }

                        if (first != null) {
                            if (!visitBlocks(dir, pos, setup.lengths[i] + zOff, visitor, first, last, metaFirst, metaLast)) {
                                return false;
                            }
                        } else {
                            if (!visitBlocks(dir, pos, setup.lengths[i] + zOff, visitor)) {
                                return false;
                            }
                        }

                        pos.isBevelBlock = false;
                        pos.bevelBlock = RkStuff.blockFusionCase;
                        pos.bevelMeta = 0;
                    }

                    pos.p = new Pos(start.x + dir.xOff, y, start.z - dir.zOff);
                    pos.isCore = false;
                    pos.isCase = Math.abs(yOff) != 2;
                    pos.isBevelBlock = pos.isCase;
                    if (pos.isBevelBlock) {
                        if (yOff == 0) {
                            pos.bevelBlock = RkStuff.blockFusionCaseBevelLarge;
                            if (dir.xOff == 1 && dir.zOff == 1) {
                                pos.bevelMeta = 1;
                            } else if (dir.xOff == 1 && dir.zOff == -1) {
                                pos.bevelMeta = 3;
                            } else if (dir.xOff == -1 && dir.zOff == 1) {
                                pos.bevelMeta = 2;
                            } else if (dir.xOff == -1 && dir.zOff == -1) {
                                pos.bevelMeta = 0;
                            }
                            pos.bevelMeta |= 0x08;
                        } else {
                            pos.bevelBlock = RkStuff.blockFusionCaseBevelSmall;
                            pos.bevelMeta = (dir.xOff == -1) ? 0x01 : 0;
                            pos.bevelMeta |= (yOff == 1) ? 0x02 : 0;
                            pos.bevelMeta |= (dir.zOff == 1) ? 0x04 : 0;
                        }
                    }
                    if (!visitBlocks(dir, pos, setup.lengths[i], visitor)) {
                        return false;
                    }
                    pos.isBevelBlock = false;

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

    private static boolean visitBlocks(FusionCoreDir dir, FusionPos start, int length, IFusionVisitor visitor, Block first, Block last, int metaFirst, int metaLast) {
        FusionPos currentPos = start.clone();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                currentPos.bevelBlock = first;
                currentPos.bevelMeta = metaFirst;
            } else if (i == length - 1) {
                currentPos.bevelBlock = last;
                currentPos.bevelMeta = metaLast;
            } else {
                currentPos.bevelBlock = start.bevelBlock;
                currentPos.bevelMeta = start.bevelMeta;
            }
            if (!visitor.visit(currentPos)) {
                return false;
            }
            currentPos.p.x += dir.xOff;
            currentPos.p.z += dir.zOff;
        }
        return true;
    }

    public static boolean iterateControl(FusionStructure setup, IFusionVisitor visitor) {
        FusionPos p = new FusionPos();
        MultiBlockHelper.Bounds extendedBounds = setup.controlBounds.clone();
        extendedBounds.setMinX(extendedBounds.getMinX() - 1);
        extendedBounds.setMinY(extendedBounds.getMinY() - 1);
        extendedBounds.setMinZ(extendedBounds.getMinZ() - 1);
        extendedBounds.setMaxX(extendedBounds.getMaxX() + 1);
        extendedBounds.setMaxY(extendedBounds.getMaxY() + 1);
        extendedBounds.setMaxZ(extendedBounds.getMaxZ() + 1);

        int xMin = extendedBounds.getMinX();
        int yMin = extendedBounds.getMinY();
        int zMin = extendedBounds.getMinZ();
        int xMax = extendedBounds.getMaxX();
        int yMax = extendedBounds.getMaxY();
        int zMax = extendedBounds.getMaxZ();
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
