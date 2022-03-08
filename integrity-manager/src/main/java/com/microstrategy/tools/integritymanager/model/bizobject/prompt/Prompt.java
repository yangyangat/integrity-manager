package com.microstrategy.tools.integritymanager.model.bizobject.prompt;


public class Prompt {
    private String id;
    private String key;
    private String type;
    private String name;
    private String title;
    private boolean required;
    private boolean closed;

    public Prompt(String id, String key, String type, String name, String title, boolean required, boolean closed) {
        this.id = id;
        this.key = key;
        this.type = type;
        this.name = name;
        this.title = title;
        this.required = required;
        this.closed = closed;
    }

    public Prompt() {
    }

    public String getId() {
        return this.id;
    }

    public String getKey() {
        return this.key;
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean isRequired() {
        return this.required;
    }

    public boolean isClosed() {
        return this.closed;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Prompt)) return false;
        final Prompt other = (Prompt) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$key = this.getKey();
        final Object other$key = other.getKey();
        if (this$key == null ? other$key != null : !this$key.equals(other$key)) return false;
        final Object this$type = this.getType();
        final Object other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$title = this.getTitle();
        final Object other$title = other.getTitle();
        if (this$title == null ? other$title != null : !this$title.equals(other$title)) return false;
        if (this.isRequired() != other.isRequired()) return false;
        if (this.isClosed() != other.isClosed()) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Prompt;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $key = this.getKey();
        result = result * PRIME + ($key == null ? 43 : $key.hashCode());
        final Object $type = this.getType();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $title = this.getTitle();
        result = result * PRIME + ($title == null ? 43 : $title.hashCode());
        result = result * PRIME + (this.isRequired() ? 79 : 97);
        result = result * PRIME + (this.isClosed() ? 79 : 97);
        return result;
    }

    public String toString() {
        return "Prompt(id=" + this.getId() + ", key=" + this.getKey() + ", type=" + this.getType() + ", name=" + this.getName() + ", title=" + this.getTitle() + ", required=" + this.isRequired() + ", closed=" + this.isClosed() + ")";
    }
}
