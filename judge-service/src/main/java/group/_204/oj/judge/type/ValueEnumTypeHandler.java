package group._204.oj.judge.type;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Enum converter, convert Enum and int value.
 */
@MappedTypes({BaseEnum.class})
public class ValueEnumTypeHandler<E extends Enum<?> & BaseEnum> extends BaseTypeHandler<E> {

    private final Class<E> type;

    public ValueEnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null.");
        }

        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, E e, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i, e.getValue());
    }

    @Override
    public E getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        if (resultSet.wasNull())
            return null;
        return valueOf(type, resultSet.getInt(columnName));
    }

    @Override
    public E getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        if (resultSet.wasNull())
            return null;
        return valueOf(type, resultSet.getInt(columnIndex));
    }

    @Override
    public E getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        if (callableStatement.wasNull())
            return null;
        return valueOf(type, callableStatement.getInt(columnIndex));
    }

    private <T extends Enum<?> & BaseEnum> T valueOf(Class<T> enumClass, int value) {
        T[] enumConstants = enumClass.getEnumConstants();

        for (T e : enumConstants) {
            if (e.getValue() == value)
                return e;
        }

        return null;
    }
}
