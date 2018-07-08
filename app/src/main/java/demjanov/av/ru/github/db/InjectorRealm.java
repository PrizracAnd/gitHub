package demjanov.av.ru.github.db;

import dagger.Component;
import demjanov.av.ru.github.presenters.Presenter;
import demjanov.av.ru.github.presenters.PresenterMoreUsers;

@Component(modules = {RealmInit.class})
public interface InjectorRealm {
    void injectToPresenter(Presenter presenter);
    void injectToPresenterMoreUsers(PresenterMoreUsers presenterMoreUsers);
}
