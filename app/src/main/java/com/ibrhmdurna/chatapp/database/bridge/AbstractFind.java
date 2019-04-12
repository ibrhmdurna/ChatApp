package com.ibrhmdurna.chatapp.database.bridge;

public abstract class AbstractFind {

    protected IFind IFind;

    public AbstractFind(IFind IFind){
        this.IFind = IFind;
    }

    public abstract void buildView();

    public abstract void getInformation();
}
