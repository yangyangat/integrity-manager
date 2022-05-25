package com.microstrategy.tools.integritymanager.model.entity.mstr.prompt;

import java.util.List;

public class AnswerPromptData {
    private String key;
    private String type;
    private String id;
    private List<PromptAnswer> answers;

    public AnswerPromptData(String key, String type, String id, List<PromptAnswer> answers) {
        this.key = key;
        this.type = type;
        this.id = id;
        this.answers = answers;
    }

    public AnswerPromptData() {
    }

    public static AnswerPromptDataBuilder builder() {
        return new AnswerPromptDataBuilder();
    }

    public String getKey() {
        return this.key;
    }

    public String getType() {
        return this.type;
    }

    public String getId() {
        return this.id;
    }

    public List<PromptAnswer> getAnswers() {
        return this.answers;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAnswers(List<PromptAnswer> answers) {
        this.answers = answers;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof AnswerPromptData)) return false;
        final AnswerPromptData other = (AnswerPromptData) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$key = this.getKey();
        final Object other$key = other.getKey();
        if (this$key == null ? other$key != null : !this$key.equals(other$key)) return false;
        final Object this$type = this.getType();
        final Object other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$answers = this.getAnswers();
        final Object other$answers = other.getAnswers();
        if (this$answers == null ? other$answers != null : !this$answers.equals(other$answers)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof AnswerPromptData;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $key = this.getKey();
        result = result * PRIME + ($key == null ? 43 : $key.hashCode());
        final Object $type = this.getType();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $answers = this.getAnswers();
        result = result * PRIME + ($answers == null ? 43 : $answers.hashCode());
        return result;
    }

    public String toString() {
        return "AnswerPromptData(key=" + this.getKey() + ", type=" + this.getType() + ", id=" + this.getId() + ", answers=" + this.getAnswers() + ")";
    }

    public static class AnswerPromptDataBuilder {
        private String key;
        private String type;
        private String id;
        private List<PromptAnswer> answers;

        AnswerPromptDataBuilder() {
        }

        public AnswerPromptDataBuilder key(String key) {
            this.key = key;
            return this;
        }

        public AnswerPromptDataBuilder type(String type) {
            this.type = type;
            return this;
        }

        public AnswerPromptDataBuilder id(String id) {
            this.id = id;
            return this;
        }

        public AnswerPromptDataBuilder answers(List<PromptAnswer> answers) {
            this.answers = answers;
            return this;
        }

        public AnswerPromptData build() {
            return new AnswerPromptData(key, type, id, answers);
        }

        public String toString() {
            return "AnswerPromptData.AnswerPromptDataBuilder(key=" + this.key + ", type=" + this.type + ", id=" + this.id + ", answers=" + this.answers + ")";
        }
    }
}
