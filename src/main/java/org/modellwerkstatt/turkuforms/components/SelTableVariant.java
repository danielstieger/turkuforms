package org.modellwerkstatt.turkuforms.components;

/**
 * Set of theme variants applicable for {@code vaadin-selection-grid} component.
 * @author Stefan Uebe
 */
public enum SelTableVariant {
    SELECTABLE_TEXT("selectable-text");

    private final String variant;

    SelTableVariant(String variant) {
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
