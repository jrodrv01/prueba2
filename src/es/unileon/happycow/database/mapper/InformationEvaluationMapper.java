/*
 * 
 */
package es.unileon.happycow.database.mapper;

import es.unileon.happycow.database.EntityDB;
import es.unileon.happycow.handler.IdEvaluation;
import es.unileon.happycow.handler.IdFarm;
import es.unileon.happycow.handler.IdHandler;
import es.unileon.happycow.model.InformationEvaluation;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author dorian
 */
public class InformationEvaluationMapper implements EntityDB {

    private InformationEvaluation info;

    public InformationEvaluationMapper(InformationEvaluation info) {
        this.info = info;
    }

    public void setInfo(InformationEvaluation info) {
        this.info = info;
    }

    @Override
    public List<PreparedStatement> insertObject(Connection connection) throws SQLException {
        PreparedStatement sql = connection.prepareStatement("INSERT INTO EVALUATION"
                + "('IDEVALUATION','IDGRANJA','USUARIO','NOTA',"
                + "'ALIMENTACION','SALUD','COMFORT','COMPORTAMIENTO',"
                + "'FECHA','NUMEROVACAS')"
                + " VALUES(?,?,?,?,?,?,?,?,?,?)");
        sql.setInt(1, Integer.parseInt(info.getIdEvaluation().toString()));
        sql.setInt(2, Integer.parseInt(info.getIdFarm().toString()));
        sql.setString(3, info.getIdUser().toString());
        sql.setFloat(4, info.getNota());
        sql.setFloat(5, info.getAlimentacion());
        sql.setFloat(6, info.getSalud());
        sql.setFloat(7, info.getComfort());
        sql.setFloat(8, info.getComportamiento());
        sql.setDate(9, info.getFecha());
        sql.setInt(10, info.getNumberCows());

        LinkedList<PreparedStatement> list = new LinkedList<>();
        list.add(sql);
        return list;
    }

    @Override
    public List<PreparedStatement> updateObject(Connection connection) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<PreparedStatement> deleteObject(Connection connection) throws SQLException {
        PreparedStatement sql = connection.prepareCall(
                "delete from evaluation where idevaluation = ?");
        sql.setInt(1, Integer.parseInt(info.getIdEvaluation().toString()));

        LinkedList<PreparedStatement> list = new LinkedList<>();
        list.add(sql);
        return list;
    }
    
    public static PreparedStatement getListEvaluations(Connection connection, IdHandler farm) throws SQLException{
        PreparedStatement sql = connection.prepareStatement("SELECT * FROM EVALUATION WHERE IDGRANJA=?");
            sql.setInt(1, Integer.parseInt(farm.toString()));
        return sql;
    }

    public static InformationEvaluation restoreObject(ResultSet result) throws SQLException {
        return new InformationEvaluation(
                (IdHandler) new IdEvaluation(result.getInt("IDEVALUATION")),
                new IdFarm(result.getInt("IDGRANJA")),
                result.getFloat("NOTA"),
                result.getFloat("ALIMENTACION"),
                result.getFloat("SALUD"),
                result.getFloat("COMFORT"),
                result.getFloat("COMPORTAMIENTO"),
                result.getDate("FECHA"),
                result.getInt("NUMEROVACAS"));
    }

}