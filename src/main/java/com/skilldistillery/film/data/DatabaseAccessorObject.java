package com.skilldistillery.film.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.skilldistillery.film.entities.Actor;
import com.skilldistillery.film.entities.Film;

@Component
public class DatabaseAccessorObject implements DatabaseAccessor {
	private static final String URL = "jdbc:mysql://localhost:3306/sdvid";

	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean deleteActor(Actor actor) {
		Connection conn = null;
		try {
			String user = "student";
			String pass = "student";
			conn = DriverManager.getConnection(URL, user, pass);
			
			conn.setAutoCommit(false); // START TRANSACTION
			
			String sql = "DELETE FROM film_actor WHERE actor_id = ?";
			
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, actor.getId());
			
			int updateCount = stmt.executeUpdate();
			
			sql = "DELETE FROM actor WHERE id = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, actor.getId());
			updateCount = stmt.executeUpdate();
			
			conn.commit(); // COMMIT TRANSACTION
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException sqle2) {
					System.err.println("Error trying to rollback");
				}
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean saveActor(Actor actor) {
		Connection conn = null;
		boolean wasSuccessful = false;

		try {
			String user = "student";
			String pass = "student";
			conn = DriverManager.getConnection(URL, user, pass);

			conn.setAutoCommit(false); // START TRANSACTION

			String sql = "UPDATE actor SET first_name=?, last_name=? WHERE id=?";

			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, actor.getFirstName());
			stmt.setString(2, actor.getLastName());
			stmt.setInt(3, actor.getId());

			int updateCount = stmt.executeUpdate();

			if (updateCount == 1) {
				// Replace actor's film list
				sql = "DELETE FROM film_actor WHERE actor_id = ?";
				stmt = conn.prepareStatement(sql);
				stmt.setInt(1, actor.getId());
				updateCount = stmt.executeUpdate();

				sql = "INSERT INTO film_actor (film_id, actor_id) VALUES (?,?)";
				stmt = conn.prepareStatement(sql);

				System.out.println("*********** actor films: " + actor.getFilms());
				if (actor.getFilms() != null) {
					for (Film film : actor.getFilms()) {
						stmt.setInt(1, film.getId());
						stmt.setInt(2, actor.getId());
						updateCount = stmt.executeUpdate();
					}
				}

				wasSuccessful = true;
				conn.commit(); // COMMIT TRANSACTION
			} else {
				wasSuccessful = false;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} // ROLLBACK TRANSACTION ON ERROR
				catch (SQLException sqle2) {
					System.err.println("Error trying to rollback");
				}
			}
			return wasSuccessful;
		}
		return wasSuccessful;
	}

	@Override
	public Actor createActor(Actor actor) {
		Connection conn = null;
		try {
			String user = "student";
			String pass = "student";
			conn = DriverManager.getConnection(URL, user, pass);

			conn.setAutoCommit(false); // START TRANSACTION

			String sql = "INSERT INTO actor (first_name, last_name) VALUES (?,?)";
			PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			stmt.setString(1, actor.getFirstName());
			stmt.setString(2, actor.getLastName());

			int updateCount = stmt.executeUpdate();

			if (updateCount == 1) {
				ResultSet keys = stmt.getGeneratedKeys();

				if (keys.next()) {
					int newActorId = keys.getInt(1);

					actor.setId(newActorId);

					if (actor.getFilms() != null && actor.getFilms().size() > 0) {
						sql = "INSERT INTO film_actor (film_id, actor_id) VALUES (?,?)";
						stmt = conn.prepareStatement(sql);

						for (Film film : actor.getFilms()) {
							stmt.setInt(1, film.getId());
							stmt.setInt(2, newActorId);
							updateCount = stmt.executeUpdate();
						}
					}
				}
				conn.commit(); // COMMIT TRANSACTION
			} else {
				actor = null;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException sqle2) {
					System.err.println("Error trying to rollback");
				}
			}
			throw new RuntimeException("Error inserting actor " + actor);
		}
		return actor;
	}

	public Actor getByWord(String aWord) {

		String user = "student";
		String pass = "student";

		Actor actor = null;

		Connection conn;
		try {
			conn = DriverManager.getConnection(URL, user, pass);

//			String sql = "SELECT * FROM actor WHERE last_name LIKE %?%"; // BAD BAD BAD
			String sql = "SELECT * FROM actor WHERE last_name LIKE ?";
			PreparedStatement aPP = conn.prepareStatement(sql);

			aPP.setString(1, "%" + aWord + "%");

			System.out.println(aPP);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Film findFilmById(int filmId) {
		Film film = null;
		Boolean getKeys = false;
		String user = "student";
		String pass = "student";

		String sql = "SELECT * FROM film WHERE id = ? ORDER BY film.id";

		try {Connection conn = DriverManager.getConnection(URL, user, pass);
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, filmId);
				ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				// populate
				film = new Film(filmId, rs.getString("title"), rs.getString("description"), rs.getInt("release_Year"),
						rs.getInt("rental_Duration"), rs.getDouble("rental_Rate"), rs.getInt("length"),
						rs.getDouble("replacement_cost"), rs.getString("rating"), rs.getString("special_Features"),
						getLanguage(rs.getInt("language_id")));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return film;
	}
	
	private String getLanguage(int id) {
		String name = null;
		boolean getKeys = false;
		String user = "student";
		String pass = "student";

		String sql = "SELECT name FROM language WHERE id = ?";
		try (Connection conn = DriverManager.getConnection(URL, user, pass);
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();) {
			if (rs.next()) {
				name = rs.getString("name");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return name;
	}

	@Override
	public List<Film> findFilmsByActorId(int actorId) {
		String user = "student";
		String pass = "student";

		// if ( films.size() == 0 ) { ...}
		// if ( films.isEmpty() ) { ... }

		List<Film> films = new ArrayList<>();
		try {
			Connection conn = DriverManager.getConnection(URL, user, pass);

			String sql = "SELECT film.* "
					+ " FROM film JOIN film_actor ON film.id = film_actor.film_id WHERE actor_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, actorId);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				int filmId = rs.getInt("id");
				String title = rs.getString("title");
				String desc = rs.getString("description");
				short releaseYear = rs.getShort("release_year");
				int langId = rs.getInt("language_id");
				int rentDur = rs.getInt("rental_duration");
				double rate = rs.getDouble("rental_rate");
				int length = rs.getInt("length");
				double repCost = rs.getDouble("replacement_cost");
				String rating = rs.getString("rating");
				String features = rs.getString("special_features");

				Film film = new Film(1, title, desc, null, length, rate, null, repCost, rating, features, features);

				films.add(film);
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return films;
	}

	@Override
	public Actor findActorById(int actorId) {
		String user = "student";
		String pass = "student";

		Actor actor = null;
		try {
			Connection conn = DriverManager.getConnection(URL, user, pass);

			String sql = "SELECT * FROM actor WHERE id = ?";

			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, actorId);
//			System.out.println(stmt);

			ResultSet actorResult = stmt.executeQuery();

			if (actorResult.next()) {
				String firstName = actorResult.getString("first_name");
				String lastName = actorResult.getString("last_name");
				actor = new Actor(actorId, firstName, lastName);
				actor.setFilms(findFilmsByActorId(actorId));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return actor;
	}

}
