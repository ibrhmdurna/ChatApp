package com.ibrhmdurna.chatapp.database.bridge;

public class Find extends AbstractFind {

    public Find(IFind IFind) {
        super(IFind);
        buildView();
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
    public void getMore() {
        IFind.getMore();
    }

    @Override
    public void onDestroy() {
        IFind.onDestroy();
    }
}
