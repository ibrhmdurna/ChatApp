package com.ibrhmdurna.chatapp.database.bridge;

public class Find extends AbstractFind {

    public Find(IFind IFind) {
        super(IFind);
    }

    @Override
    public void getInformation() {
        IFind.getInformation();
    }
}
