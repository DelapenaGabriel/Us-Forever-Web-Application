package com.usforever.dao;

import com.usforever.exception.DaoException;
import com.usforever.model.Timeline;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcTimelineDao implements TimelineDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTimelineDao (DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public List<Timeline> getAllTimeline() {
        List<Timeline> timelines = new ArrayList<>();

        String sql = "SELECT * FROM timeline;";

        try{
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
            while (result.next()){
                timelines.add(mapRowToTimeline(result));
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Cannot connect to database", e);
        }
        return timelines;
    }

    @Override
    public Timeline getTimelineById(int id) {
        Timeline timeline = null;

        String sql = "SELECT * FROM timeline WHERE id = ?;";

        try{
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
            if (result.next()){
                timeline = mapRowToTimeline(result);
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Cannot connect to database", e);
        }
        return timeline;
    }

    @Override
    public Timeline createTimeline(Timeline newTimeline) {
        Timeline timeline = null;

        int newId;

        String sql = "INSERT INTO timeline (date, title, description, img_url, icon) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING id;";

        try{
            newId = jdbcTemplate.queryForObject(sql, int.class, newTimeline.getDate(), newTimeline.getTitle(), newTimeline.getDescription(),
                    newTimeline.getImgUrl(), newTimeline.getIcon());
            timeline = getTimelineById(newId);
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Cannot connect to database", e);
        }catch (DataIntegrityViolationException e){
            throw new DaoException("Data integrity violation", e);
        }
        return timeline;
    }

    @Override
    public Timeline updateTimeline(Timeline updatedTimeline) {
        Timeline timeline = null;

        String sql = "UPDATE timeline " +
                "SET date = ?, title = ?, description = ?, img_url = ?, icon = ? " +
                "WHERE id = ?;";

        try{
            int rowsAffected = jdbcTemplate.update(sql, updatedTimeline.getDate(), updatedTimeline.getTitle(),
                    updatedTimeline.getDescription(), updatedTimeline.getImgUrl(), updatedTimeline.getIcon(), updatedTimeline.getId());
            if (rowsAffected == 0){
                throw new DaoException("Zero rows affected");
            }
            timeline = getTimelineById(updatedTimeline.getId());
        }catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return timeline;

    }

    @Override
    public int deleteTimeline(int id) {
        String sql = "DELETE FROM timeline WHERE id = ?;";

        try{
            return jdbcTemplate.update(sql, id);
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Cannot connect to database", e);
        }catch (DataIntegrityViolationException e){
            throw new DaoException("Data integrity violation", e);
        }
    }

    private Timeline mapRowToTimeline(SqlRowSet rs){
        Timeline timeline = new Timeline();
        timeline.setId(rs.getInt("id"));
        timeline.setDate(rs.getString("date"));
        timeline.setTitle(rs.getString("title"));
        timeline.setDescription(rs.getString("description"));
        timeline.setImgUrl(rs.getString("img_url"));
        timeline.setIcon(rs.getString("icon"));
        return timeline;
    }
}
