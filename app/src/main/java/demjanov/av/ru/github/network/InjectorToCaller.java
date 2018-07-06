package demjanov.av.ru.github.network;

import dagger.Component;

@Component(modules = {CreaterRestAPI.class, NetworkInfoProvider.class})
public interface InjectorToCaller {
    void injectToCaller(Caller caller);

}
