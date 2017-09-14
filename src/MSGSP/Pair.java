package MSGSP;

public class Pair<F, S> {
    private F m_First;
    private S m_Second;

    public Pair(F first, S second) {
        m_First = first;
        m_Second = second;
    }

    public void SetFirst(F first) {
        m_First = first;
    }

    public void SetSecond(S second) {
        m_Second = second;
    }

    public F GetFirst() {
        return m_First;
    }

    public S GetSecond() {
        return m_Second;
    }
}
