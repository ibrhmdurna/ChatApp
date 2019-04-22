package com.ibrhmdurna.chatapp.database.bridge;

public abstract class AbstractFindAll {

    protected IFind IFind;

    public AbstractFindAll(IFind IFind){
        this.IFind = IFind;
    }

    public abstract void buildView();

    public abstract void getContent();
}
