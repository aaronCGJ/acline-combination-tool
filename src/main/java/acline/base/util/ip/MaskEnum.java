package acline.base.util.ip;

/**
 * @author Aaron Chen
 */
public enum MaskEnum {
    /**
     *
     */
    MASK_1(1),
    MASK_2(2),
    MASK_3(3),
    MASK_4(4),
    MASK_5(5),
    MASK_6(6),
    MASK_7(7),
    MASK_8(8),
    MASK_9(9),
    MASK_10(10),
    MASK_11(11),
    MASK_12(12),
    MASK_13(13),
    MASK_14(14),
    MASK_15(15),
    MASK_16(16),
    MASK_17(17),
    MASK_18(18),
    MASK_19(19),
    MASK_20(20),
    MASK_21(21),
    MASK_22(22),
    MASK_23(23),
    MASK_24(24),
    MASK_25(25),
    MASK_26(26),
    MASK_27(27),
    MASK_28(28),
    MASK_29(29),
    MASK_30(30),
    MASK_31(31),
    MASK_32(32),
    MASK_48(48),
    MASK_127(127),
    MASK_128(128);

    private Integer mask;

    MaskEnum(Integer mask) {
        this.mask = mask;
    }

    public Integer getMask() {
        return mask;
    }

    public void setMask(Integer mask) {
        this.mask = mask;
    }
}
