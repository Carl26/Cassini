package org.x.cassini;

import java.util.ArrayList;

/**
 * Created by shuangyang on 13/7/17.
 */

public class TagBlock {
    private ArrayList<String> mTagNames;
    private char mAlphabet;
    public TagBlock(char alphabet, ArrayList<String> tagNames) {
        mAlphabet = alphabet;
        mTagNames = tagNames;
    }

    public char getmAlphabet() { return mAlphabet;}

    public void setmAlphabet(char alphabet) { this.mAlphabet = alphabet;}

    public ArrayList<String> getmTagNames() { return mTagNames;}

    public void setmTagNames(ArrayList<String> tagNames) {this.mTagNames = tagNames;}

    public int getTagCount() {return this.mTagNames.size();}

    public boolean isEmpty() {
        return getTagCount() == 0;
    }
}
