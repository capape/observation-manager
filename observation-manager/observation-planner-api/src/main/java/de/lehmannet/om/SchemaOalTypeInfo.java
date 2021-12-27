package de.lehmannet.om;

public class SchemaOalTypeInfo {

    private final String targetClassName;
    private final String targetType;
    private final String findingClassName;
    private final String findingType;

    private SchemaOalTypeInfo(Builder builder) {
        this.targetClassName = builder.targetClassName;
        this.targetType = builder.targetType;
        this.findingClassName = builder.findingClassName;
        this.findingType = builder.findingType;
    }

    public String getFindingType() {
        return findingType;
    }

    public String getFindingClassName() {
        return findingClassName;
    }

    public String getTargetType() {
        return targetType;
    }

    public String getTargetClassName() {
        return targetClassName;
    }

    public static class Builder {

        private String targetClassName;
        private String targetType;
        private String findingClassName;
        private String findingType;

        public Builder targetClassName(String value) {
            this.targetClassName = value;
            return this;
        }

        public Builder targetType(String value) {
            this.targetType = value;
            return this;
        }

        public Builder findingClassName(String value) {
            this.findingClassName = value;
            return this;
        }

        public Builder findingType(String value) {
            this.findingType = value;
            return this;
        }

        public SchemaOalTypeInfo build() {
            return new SchemaOalTypeInfo(this);
        }

    }

}
