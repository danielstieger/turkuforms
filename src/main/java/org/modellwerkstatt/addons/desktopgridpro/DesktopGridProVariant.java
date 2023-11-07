package org.modellwerkstatt.addons.desktopgridpro;

public enum DesktopGridProVariant {
    SELECTABLE_TEXT("selectable-text");

    private final String variant;

    DesktopGridProVariant(String variant) {
        this.variant = variant;
    }

    /**
     * Gets the variant name.
     *
     * @return variant name
     */
    public String getVariantName() {
        return variant;
    }
}
