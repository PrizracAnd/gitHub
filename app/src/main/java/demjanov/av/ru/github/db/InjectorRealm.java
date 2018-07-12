package demjanov.av.ru.github.db;

import dagger.Component;
import demjanov.av.ru.github.presenters.PresenterMoreUsers;

@Component(modules = {RealmInit.class})
public interface InjectorRealm {
    void injectToPresenterMoreUsers(PresenterMoreUsers presenterMoreUsers);
}
