package la1;

public enum Rating {
    UNRATED(0), ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5);

    private final int value;

    Rating(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * 根据整数值返回对应的 Rating 枚举。输入值必须在 0 到 5 之间，其中 0 表示未评分（UNRATED）。
     */
    public static Rating fromInt(int value) {
        return switch (value) {
            case 0 -> UNRATED;
            case 1 -> ONE;
            case 2 -> TWO;
            case 3 -> THREE;
            case 4 -> FOUR;
            case 5 -> FIVE;
            default -> throw new IllegalArgumentException("Rating must be between 0 and 5 (0 means unrated).");
        };
    }
}
