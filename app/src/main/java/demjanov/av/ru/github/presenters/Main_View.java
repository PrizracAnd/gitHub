package demjanov.av.ru.github.presenters;

import android.support.annotation.Nullable;

public interface Main_View {
     void startLoad();

     void endLoad();

     void setError(int number, @Nullable String message);

     void setData(int dataType);
}
