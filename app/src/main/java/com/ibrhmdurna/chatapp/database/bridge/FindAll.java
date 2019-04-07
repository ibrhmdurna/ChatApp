package com.ibrhmdurna.chatapp.database.bridge;

public class FindAll extends AbstractFindAll {

    public FindAll(IFind IFind) {
        super(IFind);
    }

    @Override
    public void getInformation() {
        IFind.getInformation();
    }
}
