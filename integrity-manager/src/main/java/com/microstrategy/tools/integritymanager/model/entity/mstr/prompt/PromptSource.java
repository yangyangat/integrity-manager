package com.microstrategy.tools.integritymanager.model.entity.mstr.prompt;


public class PromptSource {
    private String name;
    private String id;
    private int type;

    public PromptSource(String name, String id, int type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }

    public PromptSource() {
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }

    public int getType() {
        return this.type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof PromptSource)) return false;
        final PromptSource other = (PromptSource) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        if (this.getType() != other.getType()) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof PromptSource;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        result = result * PRIME + this.getType();
        return result;
    }

    public String toString() {
        return "PromptSource(name=" + this.getName() + ", id=" + this.getId() + ", type=" + this.getType() + ")";
    }
}
