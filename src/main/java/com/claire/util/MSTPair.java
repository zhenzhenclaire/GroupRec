package com.claire.util;

/**
 * Created by Claire on 1/24/2016.
 */
public class MSTPair {
    UserNode u1;
    UserNode u2;

    public MSTPair(UserNode u1, UserNode u2) {
        this.u1 = u1;
        this.u2 = u2;
    }

    public UserNode getU1() {
        return u1;
    }

    public void setU1(UserNode u1) {
        this.u1 = u1;
    }

    public UserNode getU2() {
        return u2;
    }

    public void setU2(UserNode u2) {
        this.u2 = u2;
    }
}
