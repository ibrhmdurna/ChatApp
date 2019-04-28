package com.ibrhmdurna.chatapp.database.bridge;

public class Find extends AbstractFind {

    public Find(IFind IFind) {
        super(IFind);
    }

    @Override
    public void buildView() {
        IFind.buildView();
    }

    @Override
    public void getContent() {
        IFind.getContent();
    }

    @Override
    public void onDestroy() {
        IFind.onDestroy();
    }
}
