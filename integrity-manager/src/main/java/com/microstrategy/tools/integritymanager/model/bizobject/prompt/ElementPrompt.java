package com.microstrategy.tools.integritymanager.model.bizobject.prompt;

import java.util.List;

public class ElementPrompt extends Prompt {
    private PromptSource source;
    private List<PromptElementAnswer> defaultAnswer;
    private List<PromptElementAnswer> answers;

    public ElementPrompt(PromptSource source, List<PromptElementAnswer> defaultAnswer, List<PromptElementAnswer> answers) {
        this.source = source;
        this.defaultAnswer = defaultAnswer;
        this.answers = answers;
    }

    public ElementPrompt() {
    }

    public PromptSource getSource() {
        return this.source;
    }

    public List<PromptElementAnswer> getDefaultAnswer() {
        return this.defaultAnswer;
    }

    public List<PromptElementAnswer> getAnswers() {
        return this.answers;
    }

    public void setSource(PromptSource source) {
        this.source = source;
    }

    public void setDefaultAnswer(List<PromptElementAnswer> defaultAnswer) {
        this.defaultAnswer = defaultAnswer;
    }

    public void setAnswers(List<PromptElementAnswer> answers) {
        this.answers = answers;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ElementPrompt)) return false;
        final ElementPrompt other = (ElementPrompt) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$source = this.getSource();
        final Object other$source = other.getSource();
        if (this$source == null ? other$source != null : !this$source.equals(other$source)) return false;
        final Object this$defaultAnswer = this.getDefaultAnswer();
        final Object other$defaultAnswer = other.getDefaultAnswer();
        if (this$defaultAnswer == null ? other$defaultAnswer != null : !this$defaultAnswer.equals(other$defaultAnswer))
            return false;
        final Object this$answers = this.getAnswers();
        final Object other$answers = other.getAnswers();
        if (this$answers == null ? other$answers != null : !this$answers.equals(other$answers)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ElementPrompt;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $source = this.getSource();
        result = result * PRIME + ($source == null ? 43 : $source.hashCode());
        final Object $defaultAnswer = this.getDefaultAnswer();
        result = result * PRIME + ($defaultAnswer == null ? 43 : $defaultAnswer.hashCode());
        final Object $answers = this.getAnswers();
        result = result * PRIME + ($answers == null ? 43 : $answers.hashCode());
        return result;
    }

    public String toString() {
        return "ElementPrompt(source=" + this.getSource() + ", defaultAnswer=" + this.getDefaultAnswer() + ", answers=" + this.getAnswers() + ")";
    }
}
