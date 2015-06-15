package edu.upc.eetac.dsa.dsaqp1415g6.fotoshare.api; 

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import edu.upc.eetac.dsa.dsaqp1415g6.fotoshare.api.model.Category;
import edu.upc.eetac.dsa.dsaqp1415g6.fotoshare.api.model.Photo;
import edu.upc.eetac.dsa.dsaqp1415g6.fotoshare.api.model.PhotoCollection;
import edu.upc.eetac.dsa.dsaqp1415g6.fotoshare.api.model.Review;
import edu.upc.eetac.dsa.dsaqp1415g6.fotoshare.api.model.Score;
import edu.upc.eetac.dsa.dsaqp1415g6.fotoshare.api.model.User;
 
@Path("/fotoshare") 

public class FotoshareResource {
	 
		@Context 
		private Application app; 
	 
		SecurityContext security; 
	 
		private DataSource ds = DataSourceSPA.getInstance().getDataSource(); 
	 
	 
		@GET 
		@Produces(MediaType.FOTOSHARE_API_PHOTOS_COLLECTION) 
		public PhotoCollection getPhotos() { 
			PhotoCollection photos = new PhotoCollection(); 
	 
			Connection conn = null; 
			try { 
				conn = ds.getConnection(); 
			} catch (SQLException e) { 
				throw new ServerErrorException( 
						"Could not connect to the database", 
						Response.Status.SERVICE_UNAVAILABLE); 
			} 
	 
			PreparedStatement stmt = null; 
			try { 
				String sql = buildQueryGetPhotoCollection(); 
				stmt = conn.prepareStatement(sql); 
				ResultSet rs = stmt.executeQuery(); 
	 
				while (rs.next()) { 
					Photo photo = new Photo(); 
					photo.setPhotoid(rs.getInt("photoid")); 
					photo.setPhotoname(rs.getString("photoname")); 
					photo.setUsername(rs.getString("username")); 
					photo.setDate(rs.getDate("data")); 
					photo.setFilename(rs.getInt("photoid") + ".jpg"); 
					photo.setUrl(app.getProperties().get("http://www.grupo6.dsa/img/") 
							+ photo.getFilename()); 
					int photoid = photo.getPhotoid(); 
					try { 
						System.out.println("Llegeix els comentaris"); 
						String sqlr = "select*from review where photoid = ?"; 
	 
						stmt = conn.prepareStatement(sqlr); 
						stmt.setInt(1, photoid); 
						ResultSet rs2 = stmt.executeQuery(); 
	 
						rs2 = stmt.executeQuery(); 
						while (rs2.next()) { 
							Review review = new Review(); 
							review.setFotoid(rs2.getInt("photoid")); 
							review.setReviewtext(rs2.getString("reviewtext")); 
							review.setDate(rs2.getDate("data")); 
							review.setReviewid(rs2.getInt("reviewid")); 
							review.setUsername(rs2.getString("username")); 
	 
							photo.addReview(review); 
						} 
	 
						System.out.println("Llegeix les categories"); 
	 
						String sqlc = "select * from categories where photoid=?"; 
						stmt.close(); 
						stmt = conn.prepareStatement(sqlc); 
						stmt.setInt(1, photoid); 
						rs2 = stmt.executeQuery(); 
						if (rs2.next()) { 
							Category cat = new Category(); 
							cat.setTagid(rs2.getString("tagid")); 
							cat.setCategoria(rs2.getString("category")); 
	 
							photo.addCategoria(cat); 
						} else { 
							System.out.println("No hi ha categories"); 
						} 
						System.out.println("Llegeix les puntuacions"); 
						String sqlp = "select * from scores where photoid=?"; 
						stmt.close(); 
						stmt = conn.prepareStatement(sqlp); 
						stmt.setInt(1, photoid); 
						rs2 = stmt.executeQuery(); 
						int i = 0; 
						int score = 0; 
						while (rs2.next()) { 
							Score punt = new Score();
							punt.setPuntuacionid(rs2.getInt("scoreid")); 
							punt.setPuntuacion(rs2.getInt("score")); 
							i++; 
							photo.addPuntuacion(punt); 
							score = score + punt.getPuntuacion(); 
						} 
						if (score != 0) { 
	 
							photo.setScore((int) (score / i)); 
						} 
					} catch (SQLException e) { 
						throw new ServerErrorException(e.getMessage(), 
								Response.Status.INTERNAL_SERVER_ERROR); 
					} finally { 
						System.out.println("Afegeix la foto"); 
						photos.addPhotos(photo); 
					} 
				} 
			} catch (SQLException e) { 
				throw new ServerErrorException(e.getMessage(), 
						Response.Status.INTERNAL_SERVER_ERROR); 
			} finally { 
				try { 
					if (stmt != null) 
						stmt.close(); 
					conn.close(); 
				} catch (SQLException e) { 
					throw new NotFoundException(); 
				} 
			} 
	 
			return photos; 
		} 
	 

		@GET 
		@Path("/{photoid}") 
		@Produces(MediaType.FOTOSHARE_API_PHOTOS) 
		public Photo getPhotoid(@PathParam("photoid") String photoid) { 
	 
			Photo photo = getPhotoFromDatabase(photoid); 
			return photo; 
	 
		} 
	 
		public static String md5(String clear) throws Exception { 
			MessageDigest md = MessageDigest.getInstance("MD5"); 
			byte[] b = md.digest(clear.getBytes()); 
			int size = b.length; 
			StringBuffer h = new StringBuffer(size); 
			for (int i = 0; i < size; i++) { 
				int u = b[i] & 255; 
				if (u < 16) { 
					h.append("0" + Integer.toHexString(u)); 
				} else { 
					h.append(Integer.toHexString(u)); 
				} 
			} 
	
			return h.toString(); 
		} 
	
		@POST 
		@Path("/users") 
		@Consumes(MediaType.FOTOSHARE_API_USERS) 
		@Produces(MediaType.FOTOSHARE_API_USERS) 
		public User creatUser(User usuario) throws Exception { 
	 
			System.out.println("Crea usuari"); 
			Connection conn = null; 
			try { 
				conn = ds.getConnection(); 
			} catch (SQLException e) { 
				throw new ServerErrorException("Could not connect to the database", 
						Response.Status.SERVICE_UNAVAILABLE); 
			} 
	 
			PreparedStatement stmt = null; 
			try { 
				String sql = buildCreateUser(); 
				stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); 
				stmt.setString(1, usuario.getUsername()); 
				stmt.setString(2, usuario.getPassword()); 
				stmt.setString(3, usuario.getName()); 
				stmt.setString(4, usuario.getEmail()); 
				stmt.executeUpdate(); 
				ResultSet rs = stmt.getGeneratedKeys(); 
				if (rs.next()) { 
					throw new ForbiddenException("You have got registered"); 
				} else { 
					// Something has failed... 
				} 
			} catch (SQLException e) { 
				e.printStackTrace(); 
				throw new ForbiddenException("Ja existeix"); 
			} finally { 
				try { 
					if (stmt != null) 
						stmt.close(); 
					conn.close(); 
				} catch (SQLException e) { 
					throw new ServerErrorException(e.getMessage(), 
							Response.Status.INTERNAL_SERVER_ERROR); 
				} 
			} 
			return usuario; 
		} 
	 
		private String buildCreateUser() { 
			return "insert into users (username, password, name, email) values (?, MD5(?),   ? ,?)"; 
		} 
	 

		@PUT 
		@Path("/{photoid}") 
		@Consumes(MediaType.FOTOSHARE_API_PHOTOS) 
		@Produces(MediaType.FOTOSHARE_API_PHOTOS) 
		public Photo updateBook(@PathParam("photoid") String photoid, Photo photo) { 
	 
			Connection conn = null; 
			try { 
				conn = ds.getConnection(); 
			} catch (SQLException e) { 
				throw new ServerErrorException("Could not connect to the database", 
						Response.Status.SERVICE_UNAVAILABLE); 
			} 
	 
			PreparedStatement stmt = null; 
			try { 
				
				String sql = buildUpdatePhoto(); 
				stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); 
				stmt.setString(1, photo.getPhotoname()); 
				stmt.setString(2, photoid); 
	 
				stmt.executeUpdate(); // para añadir la ficha del libro con los 
										// datos a la BBDD 
				// si ha ido bien la inserción 
				photo = getPhotoFromDatabase(photoid); 
	 
			} catch (SQLException e) { 
				e.printStackTrace(); 
			} finally { 
				try { 
					if (stmt != null) 
						stmt.close(); 
					conn.close(); 
				} catch (SQLException e) { 
					throw new ServerErrorException(e.getMessage(), 
							Response.Status.INTERNAL_SERVER_ERROR); 
				} 
			} 
	 
			return photo; 
	 
		} 
	 
		@DELETE 
		@Path("/{photoid}/{username}") 
		public void deletePhoto(@PathParam("photoid") String photoid, 
				@PathParam("username") String username) { 
	 
			try { 
				validateUserActions(username, photoid); 
			
				Connection conn = null; 
				try { 
					conn = ds.getConnection(); 
				} catch (SQLException e) { 
					throw new ServerErrorException( 
							"Could not connect to the database", 
							Response.Status.SERVICE_UNAVAILABLE); 
				} 
	 
				PreparedStatement stmt = null; 
				try { 
					
					String sql = buildDeletePhoto(); 
					stmt = conn.prepareStatement(sql, 
							Statement.RETURN_GENERATED_KEYS); 
					stmt.setString(1, photoid); 
	 
					int rows = stmt.executeUpdate(); 
	 
					if (rows == 0) { 
						throw new NotFoundException("There's no photo with photoid=" 
								+ photoid); 
					} 
				} catch (SQLException e) { 
					e.printStackTrace(); 
				} finally { 
					try { 
						if (stmt != null) 
							stmt.close(); 
						conn.close(); 
					} catch (SQLException e) { 
						throw new ServerErrorException(e.getMessage(), 
								Response.Status.INTERNAL_SERVER_ERROR); 
					} 
				} 
			} catch (Exception e1) { 
				// TODO Auto-generated catch block 
				e1.printStackTrace(); 
			} 
	 
		} 
	 
		
		private void validateUserActions(String currentUsername, String photoid) 
				throws Exception { 
			System.out.println("validateUserActions"); 
	 
			Connection conn = null; 
			try { 
				conn = ds.getConnection(); 
			} catch (SQLException e) { 
				throw new ServerErrorException("Could not connect to the database", 
						Response.Status.SERVICE_UNAVAILABLE); 
			} 
	 
			PreparedStatement stmt = null; 
			try { 
				String sql = buildValidateUser(); 
				stmt = conn.prepareStatement(sql); 
				stmt.setString(1, photoid); 
				ResultSet rs = stmt.executeQuery(); 
	 
				if (rs.next()) { 
					String username_photo = rs.getString("username"); 
	 
					if (currentUsername.equals(username_photo)) { 
						System.out 
								.println("L'usuari que intenta modificar o esborrar i el què ha pujat la foto és el mateix. Per tant, no hi ha problema."); 
	 
					} else { 
						throw new ForbiddenException( 
								"No teniu permís per modificar o eliminar aquesta fotografia."); 
					} 
				} else { 
					throw new Exception("No existeix una fotografia con aquest photoid."); 
				} 
	 
			} catch (SQLException e) { 
				e.printStackTrace(); 
			} finally { 
				try { 
					if (stmt != null) 
						stmt.close(); 
					conn.close(); 
				} catch (SQLException e) { 
					throw new ServerErrorException(e.getMessage(), 
							Response.Status.INTERNAL_SERVER_ERROR); 
				} 
			} 
	 
		} 
	 
		private String buildValidateUser() { 
			return "select username from photos where photoid=?"; 
		} 
	 
		@POST 
		@Path("/{photoid}/reviews") 
		@Consumes(MediaType.FOTOSHARE_API_REVIEWS) 
		@Produces(MediaType.FOTOSHARE_API_REVIEWS) 
		public Photo creatReview(@PathParam("photoid") String photoid, 
				Review review) { 
	 
			Photo photo = null; 
	 
			Connection conn = null; 
			try { 
				conn = ds.getConnection(); 
			} catch (SQLException e) { 
				throw new ServerErrorException("Could not connect to the database", 
						Response.Status.SERVICE_UNAVAILABLE); 
			} 
	 
			PreparedStatement stmt = null; 
			try { 
				String sql = buildCreateReview(); 
				stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); 
				stmt.setString(1, photoid); 
				stmt.setString(2, review.getUsername()); 
				stmt.setString(4, review.getReviewtext()); 
				stmt.setDate(3, review.getDate()); 
				stmt.executeUpdate(); 
	 
				ResultSet rs = stmt.getGeneratedKeys(); 
				photo = getPhotoFromDatabase(photoid); 
				if (rs.next()) { 
	 
				} else { 
					// Something has failed... 
				} 
			} catch (SQLException e) { 
				e.printStackTrace(); 
			} finally { 
				try { 
					if (stmt != null) 
						stmt.close(); 
					conn.close(); 
				} catch (SQLException e) { 
					throw new ServerErrorException(e.getMessage(), 
							Response.Status.INTERNAL_SERVER_ERROR); 
				} 
			} 
	 
			return photo; 
		} 
	 
		
		@DELETE 
		@Path("/{photoid}/reviews/{reviewid}/{username}") 
		public void deleteReview(@PathParam("photoid") String photoid, 
				@PathParam("reviewid") String reviewid, 
				@PathParam("username") String username) { 
			
			try { 
				validateUser(username, reviewid); 
				
				Connection conn = null; 
				try { 
					conn = ds.getConnection(); 
				} catch (SQLException e) { 
					throw new ServerErrorException( 
							"Could not connect to the database", 
							Response.Status.SERVICE_UNAVAILABLE); 
				} 
	 
				PreparedStatement stmt = null; 
				try { 
					
					String sql = buildDeleteReview(); 
					stmt = conn.prepareStatement(sql, 
							Statement.RETURN_GENERATED_KEYS); 
	 
					stmt.setString(1, reviewid); 
	 
					int rows = stmt.executeUpdate(); 
	 
					if (rows == 0) { 
						throw new NotFoundException( 
								"There's no review with review=" + reviewid 
										+ "with the photo with photoid=" + photoid); 
					} 
				} catch (SQLException e) { 
					e.printStackTrace(); 
				} finally { 
					try { 
						if (stmt != null) 
							stmt.close(); 
						conn.close(); 
					} catch (SQLException e) { 
						throw new ServerErrorException(e.getMessage(), 
								Response.Status.INTERNAL_SERVER_ERROR); 
					} 
				} 
			} catch (Exception e1) { 
				// TODO Auto-generated catch block 
				e1.printStackTrace(); 
			} 
		} 
	 
		// ******************* Métodos adicionales / QUERIES ********************* 
	 
		// (1)GET colección de libros 
		private String buildQueryGetPhotoCollection() { 
			return "select * from photos";
		}
	 
		// (2)GET de un libro con identificador bookid 
		private String buildQueryGetPhotoByPhotoid() { 
			return "select * from photos where photoid=?"; 
		} 
	 
		// 5.3. Método para obtener libro con bookid 
		private Photo getPhotoFromDatabase(String photoid) { 
			Connection conn = null; 
			
			Photo photo = new Photo(); 
			try { 
				conn = ds.getConnection(); 
			} catch (SQLException e) { 
				throw new ServerErrorException("Could not connect to the database", 
						Response.Status.SERVICE_UNAVAILABLE); 
			} 
	 
			PreparedStatement stmt = null; 
			try { 
				String sql = buildQueryGetPhotoByPhotoid(); 
				stmt = conn.prepareStatement(sql); 
				stmt.setString(1, photoid); 
	 
				ResultSet rs = stmt.executeQuery(); 
				if (rs.next()) { 
	 
					photo.setPhotoid(rs.getInt("photoid")); 
					photo.setPhotoname(rs.getString("photoname")); 
					photo.setUsername(rs.getString("username")); 
					photo.setDate(rs.getDate("data")); 
					photo.setFilename(rs.getInt("photoid") + ".jpg"); 
					photo.setUrl(app.getProperties().get("http://www.grupo6.dsa/img/") 
							+ photo.getFilename()); 
				} else { 
	 
				} 
	 
				String sqlr = "select*from review where photoid = ?"; 
				stmt.close(); 
				stmt = conn.prepareStatement(sqlr); 
				stmt.setString(1, photoid); 
				rs = stmt.executeQuery(); 
				while (rs.next()) { 
					Review review = new Review(); 
					review.setFotoid(rs.getInt("photoid")); 
					review.setReviewtext(rs.getString("reviewtext")); 
					review.setDate(rs.getDate("data")); 
					review.setReviewid(rs.getInt("reviewid")); 
					review.setUsername(rs.getString("username")); 
	 
					photo.addReview(review); 
				} 
	 
				String sqlc = "select*from categories where photoid=?"; 
				stmt.close(); 
				stmt = conn.prepareStatement(sqlc); 
				stmt.setString(1, photoid); 
				rs = stmt.executeQuery(); 
				while (rs.next()) { 
					Category cat = new Category(); 
					cat.setTagid(rs.getString("tagid")); 
					cat.setCategoria(rs.getString("category")); 
	 
					photo.addCategoria(cat); 
		
				} 
	 
				String sqlp = "select*from scores where photoid=?"; 
				stmt.close(); 
				stmt = conn.prepareStatement(sqlp); 
				stmt.setString(1, photoid); 
				rs = stmt.executeQuery(); 
				int i = 0; 
				int puntuacion = 0; 
				while (rs.next()) { 
					Score punt = new Score(); 
					punt.setPuntuacionid(rs.getInt("scoreid")); 
					punt.setPuntuacion(rs.getInt("score")); 
	 
					photo.addPuntuacion(punt); 
					i++; 
					puntuacion = puntuacion + rs.getInt("score"); 
				} 
				if (puntuacion != 0) { 
	 
					photo.setScore((int) (puntuacion / i)); 
				} 
			} catch (SQLException e) { 
				e.printStackTrace(); 
			} finally { 
				try { 
 
					if (stmt != null) 
						stmt.close(); 
					conn.close(); 
				} catch (SQLException e) { 
					throw new ServerErrorException(e.getMessage(), 
							Response.Status.INTERNAL_SERVER_ERROR); 
				} 
			} 
			return photo; 
		} 
	 
		
		private String buildUpdatePhoto() { 
			return "update photos set photoname = ? where photoid = ? "; 
		} 
	 
	
		private String buildDeletePhoto() { 
			return "delete from photo where photoid=?"; 
		} 
	 
		
		private String buildCreateReview() { 
			return "insert into review (photoid, username, data, reviewtext) value (?, ?, ?, ?)"; 
		} 
	 
		
		private Review getReviewFromDatabase(String reviewid) { 
			Connection conn = null; 
	 
			Review review = new Review(); 
			try { 
				conn = ds.getConnection(); 
			} catch (SQLException e) { 
				throw new ServerErrorException("Could not connect to the database", 
						Response.Status.SERVICE_UNAVAILABLE); 
			} 
	 
			PreparedStatement stmt = null; 
			try { 
				String sql = buildQueryGetReviewByReviewid(); 
				stmt = conn.prepareStatement(sql); 
				stmt.setInt(1, Integer.parseInt(reviewid)); 
	 
				ResultSet rs = stmt.executeQuery(); 
				while (rs.next()) { 
					review.setFotoid(rs.getInt("photoid")); 
					review.setUsername(rs.getString("username")); 
					review.setReviewid(rs.getInt("reviewid")); 
					review.setDate(rs.getDate("data")); 
				
				} 
			} catch (SQLException e) { 
				e.printStackTrace(); 
			} finally { 
				try { 
				 
					if (stmt != null) 
						stmt.close(); 
					conn.close(); 
				} catch (SQLException e) { 
					throw new ServerErrorException(e.getMessage(), 
							Response.Status.INTERNAL_SERVER_ERROR); 
				} 
			} 
			return review; 
	 
		} 
	 
		
		private String buildQueryGetReviewByReviewid() { 
			return "select*from review where reviewid=?"; 
		} 
	 
		@SuppressWarnings("unused")
		private String getReviewsFromDatabaseByPhotoid(String photoid) { 
			return "select username from review where review.photoid = " + photoid; 
		} 
	 
		
		private void validateUser(String Username, String reviewid) { 
		
			Review currentReview = getReviewFromDatabase(reviewid); 
			if (Username.equals(currentReview.getUsername())) { 
				System.out 
						.println("L'usuari que intenta modificar o esborrar i el què ha pujat la fotografia és el mateix. Per tant, tot correcte."); 
	 
			} else { 
				throw new ForbiddenException( 
						"No pots modificar o esborrar aquest comentari."); 
			} 
		} 
	 
		private int validateUserPunt(String photoid, String usuario) 
				throws SQLException { 
			
			Connection conn = null; 
			try { 
				conn = ds.getConnection(); 
			} catch (SQLException e) { 
				throw new ServerErrorException("Could not connect to the database", 
						Response.Status.SERVICE_UNAVAILABLE); 
			} 
			PreparedStatement stmt = null; 
			String sql = "select * from scores where photoid = ? and username = ? "; 
			stmt = conn.prepareStatement(sql); 
			stmt.setString(1, photoid); 
			stmt.setString(2, usuario); 
			ResultSet rs = stmt.executeQuery(); 
			if (rs.next()) { 
	 
				return 0; 
			} 
	 
			else { 
				return 1; 
			} 
		} 
	 
	
		private String buildDeleteReview() { 
			return "delete from review where reviewid=?"; 
		} 
	 
		
		/* @POST 
		@Path("/upload/{username}") 
		@Consumes(MediaType.MULTIPART_FORM_DATA) 
		public Photo uploadPhoto(@FormDataParam("title") String title, 
				@FormDataParam("photo") InputStream video, 
				@FormDataParam("photo") FormDataContentDisposition fileDisposition, 
				@FormDataParam("category") String category, 
				@PathParam("username") String username) { 
	 
			String donde = fileDisposition.getFileName(); 
			System.out.println(donde); 
			Photo photo1 = null; 
			String filename; 
			int photoid; 
			Connection conn = null; 
			try { 
				conn = ds.getConnection(); 
			} catch (SQLException e) { 
				throw new ServerErrorException("Could not connect to the database", 
						Response.Status.SERVICE_UNAVAILABLE); 
			} 
			PreparedStatement stmt = null; 
			PreparedStatement stmt2 = null; 
			try { 
				String sql = "insert into photos (photoname, username, data) values (?,?, now())"; 
				String sql2 = "insert into categories(photoid, category) values(?,?)"; 
				stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); 
				stmt2 = conn.prepareStatement(sql2); 
				stmt.setString(1, title); 
	 
				stmt.setString(2, username); 
				System.out.println("Query:" + stmt); 
				stmt.executeUpdate(); 
	 
				ResultSet rs = stmt.getGeneratedKeys(); 
				if (rs.next()) { 
					photoid = rs.getInt(1); 
					System.out.println(photoid); 
					filename = Integer.toString(photoid) + ".jpg"; 
					String file = app.getProperties().get("/var/www/grupo6.dsa/public_html/img/") 
							+ filename; 
					FileCopy(video, file); 
					photo1 = getPhotoFromDatabase(Integer.toString(photoid)); 
					stmt2.setInt(1, photoid); 
					stmt2.setString(2, category); 
					System.out.println("Query:" + stmt2); 
					stmt2.executeUpdate(); 
				} else { 
	 
					// Something has failed... 
				} 
	 
				// se utiliza el método sting para pasarle el stingid 
				// para crear el sting -> JSON 
			} catch (SQLException e) { 
				throw new ServerErrorException(e.getMessage(), 
						Response.Status.INTERNAL_SERVER_ERROR); 
			} finally { 
				try { 
					if (stmt != null) 
						stmt.close(); 
					conn.close(); 
				} catch (SQLException e) { 
				} 
	 
			} 
	 
			// ImageData imageData = new ImageData(); 
			// imageData.setFilename(uuid.toString() + ".png"); 
			// imageData.setTitle(title); 
			// 
			// imageData.setImageURL(app.getProperties().get("imgBaseURL") 
			// + imageData.getFilename()); 
			// 
			return photo1; 
	 
		} */
	 
		
		public void FileCopy(InputStream in, String destinationFile) { 
			// System.out.println("Desde: " + sourceFile); 
			System.out.println("Hacia: " + destinationFile); 
	 
			try { 
				// File inFile = new File(sourceFile); 
				File outFile = new File(destinationFile); 
	 
				// InputStream in = new FileInputStream(inFile); 
				FileOutputStream out = new FileOutputStream(outFile); 
	 
				int c; 
				while ((c = in.read()) != -1) { 
					out.write(c); 
				} 
	 
				in.close(); 
				out.close(); 
			} catch (IOException e) { 
				System.err.println("Error entrada/sortida"); 
			} 
		} 
	 
		
		@GET 
		@Path("/search") 
		@Produces(MediaType.FOTOSHARE_API_PHOTOS_COLLECTION) 
		public PhotoCollection searchphoto(@QueryParam("title") String titol) { 
			System.out.println("Entrem a la funció"); 
			PhotoCollection photos = new PhotoCollection(); 
	 
			Connection conn = null; 
			try { 
				conn = ds.getConnection(); 
			} catch (SQLException e) { 
				throw new ServerErrorException( 
						"Could not connect to the database", 
						Response.Status.SERVICE_UNAVAILABLE); 
			} 
			PreparedStatement stmt = null; 
			try { 
				String sql = buildQueryGetPhotoBytitulo(); 
				stmt = conn.prepareStatement(sql); 
				System.out.println(titol); 
				stmt.setString(1, '%' + titol + '%'); 
				System.out.println("Query:" + stmt); 
				ResultSet rs = stmt.executeQuery(); 
	 
				
				if (rs.next()) { 
					
					Photo photo2 = getPhotoFromDatabase(rs.getString("photoid")); 
					System.out.println(photo2.getUsername()); 
					photos.addPhotos(photo2); 
					while (rs.next()) { 
						photo2 = getPhotoFromDatabase(rs.getString("photoid")); 
						System.out.println(photo2.getUsername()); 
						photos.addPhotos(photo2); 
					} 
				} else { 
	 
					System.out.println("No existeix una fotografia amb aquest nom"); 
					throw new ServerErrorException( 
							"No existeix una fotografia amb aquest nom", 
							Response.Status.INTERNAL_SERVER_ERROR); 
				} 
			} catch (SQLException e) { 
				e.printStackTrace(); 
			} finally { 
				try { 
					
					if (stmt != null) 
						stmt.close(); 
					conn.close(); 
				} catch (SQLException e) { 
					throw new ServerErrorException(e.getMessage(), 
							Response.Status.INTERNAL_SERVER_ERROR); 
				} 
			} 
	 
			return photos; 
	 
		} 
	 
		// método para buscar y obtener libro a partir de la categoria: 
	 
		private String buildQueryGetPhotoBytitulo() { 
			String sql = "select * from photos where photoname like ?"; 
			System.out.println("Query:" + sql); 
			return sql; 
		} 
	 
		@POST 
		@Path("/{photoid}/score") 
		@Consumes(MediaType.FOTOSHARE_API_SCORE) 
		
		public Photo insterPuntuacion(@PathParam("photoid") String photoid, 
				Score Puntos) throws SQLException { 
	 
			Photo photop = new Photo(); 
			String username = Puntos.getUsername(); 
			Score puntuacion = Puntos; 
	 
			if (validateUserPunt(photoid, username) == 0) { 
				throw new ServerErrorException( 
						"You have already qualified this photo!", 
						Response.Status.SERVICE_UNAVAILABLE); 
			} 
	 
			
			int punt = puntuacion.getPuntuacion(); 
	 
			Connection conn = null; 
			try { 
				conn = ds.getConnection(); 
			} catch (SQLException e) { 
				throw new ServerErrorException("Could not connect to the database", 
						Response.Status.SERVICE_UNAVAILABLE); 
			} 
	 
			PreparedStatement stmt = null; 
			try { 
				String sql = buildQueryInsertScore(); 
				stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); 
				stmt.setInt(1, puntuacion.getFotoid()); 
				stmt.setString(2, puntuacion.getUsername()); 
				stmt.setInt(3, punt); 
	 
				stmt.executeUpdate(); 
				ResultSet rs = stmt.getGeneratedKeys(); 
				if (rs.next()) { 
					
					photop = getPhotoFromDatabase(photoid); 
				} else { 
					// Something has failed... 
				} 
			} catch (SQLException e) { 
				e.printStackTrace(); 
			} finally { 
				try { 
					if (stmt != null) 
						stmt.close(); 
					conn.close(); 
				} catch (SQLException e) { 
					throw new ServerErrorException(e.getMessage(), 
							Response.Status.INTERNAL_SERVER_ERROR); 
				} 
			} 
	 
			return photop; 
			
		} 
	 
		private String buildQueryInsertScore() { 
			return "insert into scores (photoid, username, score) value (?,?,?) "; 
		} 
	 
		@SuppressWarnings("unused")
		private int getScoreFromPhotoid(String photoid) { 
	 
			int puntos = 0; 
	 
			Connection conn = null; 
			try { 
				conn = ds.getConnection(); 
			} catch (SQLException e) { 
				throw new ServerErrorException("Could not connect to the database", 
						Response.Status.SERVICE_UNAVAILABLE); 
			} 
	 
			PreparedStatement stmt = null; 
			try { 
				String sql = "select score from scores where photoid=" 
						+ photoid; 
				stmt = conn.prepareStatement(sql); 
	 
				ResultSet rs = stmt.executeQuery(); 
	 
				if (rs.next()) { 
					puntos = rs.getInt("score"); 
					System.out.println("S'ha obtingut la qualificació de la foto: " 
							+ puntos); 
				} else { 
					// Something has failed... 
					// throw new NotFoundException(); 
					System.out 
							.println("No s'ha pogut trobar la puntuació a la tabla"); 
					puntos = 0; 
				} 
			} catch (SQLException e) { 
				e.printStackTrace(); 
			} finally { 
				try { 
					if (stmt != null) 
						stmt.close(); 
					conn.close(); 
				} catch (SQLException e) { 
					throw new ServerErrorException(e.getMessage(), 
							Response.Status.INTERNAL_SERVER_ERROR); 
				} 
			} 
	 
			
			return puntos; 
		} 
	 
		@GET 
		@Path("/byscore") 
		@Produces(MediaType.FOTOSHARE_API_PHOTOS_COLLECTION) 
		public PhotoCollection getPhotosOrderByPuntuacion() { 
			PhotoCollection photos = getPhotos(); 
			
			photos.ordenar();
			return photos; 
		} 
	 
	 
		@GET 
		@Path("/bycategory") 
		@Produces(MediaType.FOTOSHARE_API_PHOTOS_COLLECTION) 
		public PhotoCollection getPhotosOrderByCategory() { 
			PhotoCollection photos = new PhotoCollection(); 
			Photo photo = new Photo(); 
		
			Connection conn = null; 
			try { 
				conn = ds.getConnection(); 
			} catch (SQLException e) { 
				throw new ServerErrorException( 
						"Could not connect to the database", 
						Response.Status.SERVICE_UNAVAILABLE); 
			} 
	 
			PreparedStatement stmt = null; 
			try { 
				String sql = buildQueryGetPhotosOrderByCategory(); 
				stmt = conn.prepareStatement(sql); 
			
				ResultSet rs = stmt.executeQuery(); 
	 
				while (rs.next()) { 
					try { 
	 
						String photoid = rs.getString("photoid"); 
	 
					
						photo = getPhotoFromDatabase(photoid); 
	 
					} catch (SQLException e) { 
						throw new ServerErrorException(e.getMessage(), 
								Response.Status.INTERNAL_SERVER_ERROR); 
					} finally { 
						System.out.println("Ara afegeixo la foto"); 
						photos.addPhotos(photo); 
					} 
				} 
			} catch (SQLException e) { 
				throw new ServerErrorException(e.getMessage(), 
						Response.Status.INTERNAL_SERVER_ERROR); 
			} finally { 
				try { 
					if (stmt != null) 
						stmt.close(); 
					conn.close(); 
				} catch (SQLException e) { 
					throw new NotFoundException(); 
				} 
			} 
	 
			return photos; 
		} 
	 
		// (1)GET colección de libros 
		private String buildQueryGetPhotosOrderByCategory() { 
			return "select * from categorias order by category"; 
		} 
	 
		
		@GET 
		@Path("/bydata") 
		@Produces(MediaType.FOTOSHARE_API_PHOTOS_COLLECTION) 
		public PhotoCollection getPhotosOrderByDate() { 
			PhotoCollection photos = new PhotoCollection(); 
			Photo photo = new Photo(); 
		
			Connection conn = null; 
			try { 
				conn = ds.getConnection(); 
			} catch (SQLException e) { 
				throw new ServerErrorException( 
						"Could not connect to the database", 
						Response.Status.SERVICE_UNAVAILABLE); 
			} 
	 
			PreparedStatement stmt = null; 
			try { 
				String sql = buildQueryGetPhotosOrderByCategory(); 
				stmt = conn.prepareStatement(sql); 
		
				ResultSet rs = stmt.executeQuery(); 
	 
				while (rs.next()) { 
					try { 
	 
						String photoid = rs.getString("photoid"); 
	 
					 
						photo = getPhotoFromDatabase(photoid); 
	 
					} catch (SQLException e) { 
						throw new ServerErrorException(e.getMessage(), 
								Response.Status.INTERNAL_SERVER_ERROR); 
					} finally { 
						System.out.println("Afegeix la foto"); 
						photos.addPhotos(photo); 
					} 
				} 
			} catch (SQLException e) { 
				throw new ServerErrorException(e.getMessage(), 
						Response.Status.INTERNAL_SERVER_ERROR); 
			} finally { 
				try { 
					if (stmt != null) 
						stmt.close(); 
					conn.close(); 
				} catch (SQLException e) { 
					throw new NotFoundException(); 
				} 
			} 
	 
			return photos; 
		} 
	 
		// (1)GET colección de libros 
		@SuppressWarnings("unused")
		private String buildQueryGetPhotosOrderByDate() { 
			return "select * from photos order by data"; 
		} 
	 
		@GET 
		@Path("/byusername") 
		@Produces(MediaType.FOTOSHARE_API_PHOTOS_COLLECTION) 
		public PhotoCollection getPhotosOrderByUsername() { 
			PhotoCollection photos = new PhotoCollection(); 
			Photo photo = new Photo(); 
		
			Connection conn = null; 
			try { 
				conn = ds.getConnection(); 
			} catch (SQLException e) { 
				throw new ServerErrorException( 
						"Could not connect to the database", 
						Response.Status.SERVICE_UNAVAILABLE); 
			} 
	 
			PreparedStatement stmt = null; 
			try { 
				String sql = buildQueryGetPhotosOrderUsername(); 
				stmt = conn.prepareStatement(sql); 
				
				ResultSet rs = stmt.executeQuery(); 
	 
				while (rs.next()) { 
					try { 
	 
						String photoid = rs.getString("photoid"); 
	 
						photo = getPhotoFromDatabase(photoid); 
	 
					} catch (SQLException e) { 
						throw new ServerErrorException(e.getMessage(), 
								Response.Status.INTERNAL_SERVER_ERROR); 
					} finally { 
						System.out.println("Ara afegeixo la foto"); 
						photos.addPhotos(photo); 
					} 
				} 
			} catch (SQLException e) { 
				throw new ServerErrorException(e.getMessage(), 
						Response.Status.INTERNAL_SERVER_ERROR); 
			} finally { 
				try { 
					if (stmt != null) 
						stmt.close(); 
					conn.close(); 
				} catch (SQLException e) { 
					throw new NotFoundException(); 
				} 
			} 
	 
			return photos; 
		} 
	 
		// (1)GET colección de libros 
		private String buildQueryGetPhotosOrderUsername() { 
			return "select * from photos order by username"; 
		} 
	 
		@GET 
		@Path("/searchc") 
		@Produces(MediaType.FOTOSHARE_API_PHOTOS_COLLECTION) 
		public PhotoCollection getPhotosByCategory( 
				@QueryParam("category") String category) { 
			PhotoCollection photos = new PhotoCollection(); 
			Photo photo = new Photo(); 
			// hacemos la conexión a la base de datos 
			Connection conn = null; 
			try { 
				conn = ds.getConnection(); 
			} catch (SQLException e) { 
				throw new ServerErrorException( 
						"Could not connect to the database", 
						Response.Status.SERVICE_UNAVAILABLE); 
			} 
	 
			PreparedStatement stmt = null; 
			try { 
				String sql = buildQueryGetPhotosByCategoria(); 
				stmt = conn.prepareStatement(sql); 
				stmt.setString(1, category); 
			
				ResultSet rs = stmt.executeQuery(); 
	 
				while (rs.next()) { 
					try { 
						System.out 
								.println("He obtingut la foto a partir de la categoria"); 
						String photoid = rs.getString("photoid"); 
	 
					
						photo = getPhotoFromDatabase(photoid); 
						System.out.println("S'ha afegit a la coŀlecció"); 
	 
					} catch (SQLException e) { 
						throw new ServerErrorException(e.getMessage(), 
								Response.Status.INTERNAL_SERVER_ERROR); 
					} finally { 
						System.out.println("Afegeixo la foto"); 
						photos.addPhotos(photo); 
					} 
				} 
			} catch (SQLException e) { 
				throw new ServerErrorException(e.getMessage(), 
						Response.Status.INTERNAL_SERVER_ERROR); 
			} finally { 
				try { 
					if (stmt != null) 
						stmt.close(); 
					conn.close(); 
				} catch (SQLException e) { 
					throw new NotFoundException(); 
				} 
			} 
	 
			return photos; 
		} 
	 
		// (1)GET colección de libros 
		private String buildQueryGetPhotosByCategoria() { 
			return "select * from categorias where categoria = ?"; 
		} 
	 
	
		@POST 
		@Path("/login") 
		@Consumes(MediaType.FOTOSHARE_API_USERS) 
		@Produces(MediaType.FOTOSHARE_API_USERS) 
		public User loginUser(User usuario) throws Exception { 
	 
			
			Connection conn = null; 
			try { 
				conn = ds.getConnection(); 
			} catch (SQLException e) { 
				throw new ServerErrorException("Could not connect to the database", 
						Response.Status.SERVICE_UNAVAILABLE); 
			} 
	 
			String userlogpass = md5(usuario.getPassword()); 
	 
			PreparedStatement stmt = null; 
			try { 
				String sql = buildLoginUser(); 
				stmt = conn.prepareStatement(sql); 
				stmt.setString(1, usuario.getUsername()); 
				ResultSet rs = stmt.executeQuery(); 
	 
				if (rs.next()) { 
				
					String userpass = rs.getString("password"); 
	 
					if (userlogpass.equals(userpass)) { 
						System.out.println("L'usuari i la contrasenya són correctes"); 
					} else { 
						System.out.println("L'usuari i/o la contrasenya són incorrectes"); 
						throw new Exception("Error d'inici de sessió"); 
					} 
				} else { 
					throw new Exception( 
							"Cap usuari amb aquest nom d'usuari i contrasenya"); 
				} 
			} catch (SQLException e) { 
				e.printStackTrace(); 
			} finally { 
				try { 
					if (stmt != null) 
						stmt.close(); 
					conn.close(); 
				} catch (SQLException e) { 
					throw new ServerErrorException(e.getMessage(), 
							Response.Status.INTERNAL_SERVER_ERROR); 
				} 
			} 
			return usuario; 
		} 
	 
		private String buildLoginUser() { 
			return "select * from users where username=?"; 
		} 
	 
	} 

