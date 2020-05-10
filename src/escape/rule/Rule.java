package escape.rule;

public class Rule {
    RuleID id;
    int intValue;
    public Rule() {}
    public RuleID getId() { return id; }
    public void setId(RuleID id) { this.id = id; }
    public int getIntValue() { return intValue; }
    public void setIntValue(int intValue) { this.intValue = intValue; }
    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    { return "Rule [id=" + id + ", intValue=" + intValue + "]"; }
}

