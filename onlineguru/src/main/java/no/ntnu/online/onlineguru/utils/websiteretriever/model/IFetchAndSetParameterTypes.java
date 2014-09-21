package no.ntnu.online.onlineguru.utils.websiteretriever.model;

public interface IFetchAndSetParameterTypes {
    public ISetReturnObjects setParameterTypes(Class... parameterTypes);
    public void fetch();
}
