package org.tmotte.pm;
public enum Duration {
    _128(Divisions.reg128),
    _64(Divisions.reg64),
    _32(Divisions.reg32),
    _16(Divisions.reg16),
    _8(Divisions.reg8),
    _4(Divisions.reg4),
    _2(Divisions.reg2),
    _1(Divisions.whole),

    _64dot(Divisions.reg64 + Divisions.reg128),
    _32dot(Divisions.reg32 + Divisions.reg64),
    _16dot(Divisions.reg16 + Divisions.reg32),
    _8dot(Divisions.reg8 + Divisions.reg16),
    _4dot(Divisions.reg4 + Divisions.reg8),
    _2dot(Divisions.reg2 + Divisions.reg4),
    _1dot(Divisions.whole +Divisions.reg2),

    _643(Divisions.triplet64),
    _323(Divisions.triplet32),
    _163(Divisions.triplet16),
    _83(Divisions.triplet8),

    _643dot(Divisions.triplet64 + Divisions.triplet128),
    _323dot(Divisions.triplet32 + Divisions.triplet64),
    _163dot(Divisions.triplet16 + Divisions.triplet32),
    _83dot(Divisions.triplet8   + Divisions.triplet16)

    ;

    private final long ticks;
    Duration(long ticks) {
        this.ticks=ticks;
    }

    public long duration() {
        return ticks;
    }

}