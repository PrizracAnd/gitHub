package demjanov.av.ru.github.network;

import dagger.Component;

@Component(modules = {CreaterRestAPI.class})
public interface InjectorRestAPI {
    void injectToCaller(Caller caller);
}
