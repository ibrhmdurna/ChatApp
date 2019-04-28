package com.ibrhmdurna.chatapp.database.bridge;

public class FindAll extends AbstractFindAll {

    public FindAll(IFind IFind) {
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
    public void getMore() {
        IFind.getMore();
    }

    @Override
    public void onDestroy() {
        IFind.onDestroy();
    }
}
