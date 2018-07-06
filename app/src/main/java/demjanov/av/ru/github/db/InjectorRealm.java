package demjanov.av.ru.github.db;

import dagger.Component;
import demjanov.av.ru.github.presenters.Presenter;

@Component(modules = {RealmInit.class})
public interface InjectorRealm {
    void injectToPresenter(Presenter presenter);
}
