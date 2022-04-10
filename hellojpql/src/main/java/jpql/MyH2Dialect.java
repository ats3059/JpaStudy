package jpql;

import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class MyH2Dialect extends H2Dialect {

    public MyH2Dialect() {
        registerFunction("concat_ws", new StandardSQLFunction("concat_ws", StandardBasicTypes.STRING));
    }
}
