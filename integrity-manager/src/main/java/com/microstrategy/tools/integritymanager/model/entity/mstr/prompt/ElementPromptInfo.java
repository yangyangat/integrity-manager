package com.microstrategy.tools.integritymanager.model.entity.mstr.prompt;


import java.util.List;

public class ElementPromptInfo extends PromptInfo{
    private List<PromptElementAnswer> elements;

    public ElementPromptInfo(List<PromptElementAnswer> elements) {
        this.elements = elements;
    }

    public ElementPromptInfo() {
    }

    public List<PromptElementAnswer> getElements() {
        return this.elements;
    }

    public void setElements(List<PromptElementAnswer> elements) {
        this.elements = elements;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ElementPromptInfo)) return false;
        final ElementPromptInfo other = (ElementPromptInfo) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$elements = this.getElements();
        final Object other$elements = other.getElements();
        if (this$elements == null ? other$elements != null : !this$elements.equals(other$elements)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ElementPromptInfo;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $elements = this.getElements();
        result = result * PRIME + ($elements == null ? 43 : $elements.hashCode());
        return result;
    }

    public String toString() {
        return "ElementPromptInfo(elements=" + this.getElements() + ")";
    }
}
