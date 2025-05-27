package servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Estudiante;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author ANDREA
 */
@WebServlet(name = "EstudianteCrud", urlPatterns = {"/estudiante"})
public class EstudianteCrud extends HttpServlet {

    private EntityManagerFactory emf;

    @Override
    public void init() throws ServletException {
        // Inicialización única del EntityManagerFactory
        emf = Persistence.createEntityManagerFactory("com.mycompany_Preg01_war_1.0-SNAPSHOTPU");
    }

    @Override
    public void destroy() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");

        try {
            if ("list".equals(action)) {
                listStudents(out);
            } else if ("get".equals(action)) {
                String idStr = request.getParameter("id");
                if (idStr != null) {
                    getStudent(Long.parseLong(idStr), out);
                } else {
                    sendErrorResponse(out, "ID requerido");
                }
            } else {
                sendErrorResponse(out, "Acción no válida");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(out, "Error interno del servidor");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");

        try {
            if ("add".equals(action)) {
                addStudent(request, out);
            } else if ("update".equals(action)) {
                updateStudent(request, out);
            } else if ("delete".equals(action)) {
                String idStr = request.getParameter("id");
                if (idStr != null) {
                    deleteStudent(Long.parseLong(idStr), out);
                } else {
                    sendErrorResponse(out, "ID requerido");
                }
            } else if ("changePassword".equals(action)) {
                changePassword(request, out);
            } else {
                sendErrorResponse(out, "Acción no válida");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(out, "Error interno del servidor");
        }
    }

    private void listStudents(PrintWriter out) {
        EntityManager em = emf.createEntityManager();
        try {
            Query query = em.createQuery("SELECT e FROM Estudiante e ORDER BY e.codiEstdWeb");
            List<Estudiante> estudiantes = query.getResultList();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("students", estudiantes);

            Gson gson = new Gson();
            out.print(gson.toJson(response));

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(out, "Error al obtener la lista de estudiantes");
        } finally {
            em.close();
        }
    }

    private void getStudent(Long id, PrintWriter out) {
        EntityManager em = emf.createEntityManager();
        try {
            Estudiante estudiante = em.find(Estudiante.class, id);

            if (estudiante != null) {
                // Crear un objeto con los datos del estudiante para edición
                Map<String, Object> studentData = new HashMap<>();
                studentData.put("id", estudiante.getCodiEstdWeb());
                studentData.put("dni", estudiante.getNdniEstdWeb());
                studentData.put("login", estudiante.getLogiEstd());
                studentData.put("apPaterno", estudiante.getAppaEstdWeb());
                studentData.put("apMaterno", estudiante.getApmaEstdWeb());
                studentData.put("nombre", estudiante.getNombEstdWeb());

                // Formatear fecha para el input date
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                studentData.put("fechaNacimiento", dateFormat.format(estudiante.getFechNaciEstdWeb()));

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("student", studentData);

                Gson gson = new Gson();
                out.print(gson.toJson(response));
            } else {
                sendErrorResponse(out, "Estudiante no encontrado");
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(out, "Error al obtener el estudiante");
        } finally {
            em.close();
        }
    }

    private void addStudent(HttpServletRequest request, PrintWriter out) {
        EntityManager em = emf.createEntityManager();
        try {
            // Validar que no exista el DNI
            String dni = request.getParameter("dni");
            String login = request.getParameter("login");

            Query checkDni = em.createQuery("SELECT COUNT(e) FROM Estudiante e WHERE e.ndniEstdWeb = :dni");
            checkDni.setParameter("dni", dni);
            Long dniCount = (Long) checkDni.getSingleResult();

            if (dniCount > 0) {
                sendErrorResponse(out, "Ya existe un estudiante con ese DNI");
                return;
            }

            Query checkLogin = em.createQuery("SELECT COUNT(e) FROM Estudiante e WHERE e.logiEstd = :login");
            checkLogin.setParameter("login", login);
            Long loginCount = (Long) checkLogin.getSingleResult();

            if (loginCount > 0) {
                sendErrorResponse(out, "Ya existe un estudiante con ese login");
                return;
            }

            em.getTransaction().begin();

            Estudiante estudiante = new Estudiante();
            estudiante.setNdniEstdWeb(dni);
            estudiante.setLogiEstd(login);
            estudiante.setAppaEstdWeb(request.getParameter("apPaterno"));
            estudiante.setApmaEstdWeb(request.getParameter("apMaterno"));
            estudiante.setNombEstdWeb(request.getParameter("nombre"));
            estudiante.setPassEstd(request.getParameter("password"));

            // Convertir fecha
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaNacimiento = dateFormat.parse(request.getParameter("fechaNacimiento"));
            estudiante.setFechNaciEstdWeb(fechaNacimiento);

            em.persist(estudiante);
            em.getTransaction().commit();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Estudiante agregado exitosamente");

            Gson gson = new Gson();
            out.print(gson.toJson(response));

        } catch (ParseException e) {
            em.getTransaction().rollback();
            sendErrorResponse(out, "Formato de fecha inválido");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            sendErrorResponse(out, "Error al agregar el estudiante");
        } finally {
            em.close();
        }
    }

    private void updateStudent(HttpServletRequest request, PrintWriter out) {
        EntityManager em = emf.createEntityManager();
        try {
            Long id = Long.parseLong(request.getParameter("id"));
            String dni = request.getParameter("dni");
            String login = request.getParameter("login");

            // Validar que no exista otro estudiante con el mismo DNI o login
            Query checkDni = em.createQuery("SELECT COUNT(e) FROM Estudiante e WHERE e.ndniEstdWeb = :dni AND e.codiEstdWeb != :id");
            checkDni.setParameter("dni", dni);
            checkDni.setParameter("id", id);
            Long dniCount = (Long) checkDni.getSingleResult();

            if (dniCount > 0) {
                sendErrorResponse(out, "Ya existe otro estudiante con ese DNI");
                return;
            }

            Query checkLogin = em.createQuery("SELECT COUNT(e) FROM Estudiante e WHERE e.logiEstd = :login AND e.codiEstdWeb != :id");
            checkLogin.setParameter("login", login);
            checkLogin.setParameter("id", id);
            Long loginCount = (Long) checkLogin.getSingleResult();

            if (loginCount > 0) {
                sendErrorResponse(out, "Ya existe otro estudiante con ese login");
                return;
            }

            em.getTransaction().begin();

            Estudiante estudiante = em.find(Estudiante.class, id);
            if (estudiante != null) {
                estudiante.setNdniEstdWeb(dni);
                estudiante.setLogiEstd(login);
                estudiante.setAppaEstdWeb(request.getParameter("apPaterno"));
                estudiante.setApmaEstdWeb(request.getParameter("apMaterno"));
                estudiante.setNombEstdWeb(request.getParameter("nombre"));

                // Convertir fecha
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date fechaNacimiento = dateFormat.parse(request.getParameter("fechaNacimiento"));
                estudiante.setFechNaciEstdWeb(fechaNacimiento);

                em.merge(estudiante);
                em.getTransaction().commit();

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Estudiante actualizado exitosamente");

                Gson gson = new Gson();
                out.print(gson.toJson(response));
            } else {
                em.getTransaction().rollback();
                sendErrorResponse(out, "Estudiante no encontrado");
            }

        } catch (ParseException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            sendErrorResponse(out, "Formato de fecha inválido");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            sendErrorResponse(out, "Error al actualizar el estudiante");
        } finally {
            em.close();
        }
    }

    private void deleteStudent(Long id, PrintWriter out) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Estudiante estudiante = em.find(Estudiante.class, id);
            if (estudiante != null) {
                em.remove(estudiante);
                em.getTransaction().commit();

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Estudiante eliminado exitosamente");

                Gson gson = new Gson();
                out.print(gson.toJson(response));
            } else {
                em.getTransaction().rollback();
                sendErrorResponse(out, "Estudiante no encontrado");
            }

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            sendErrorResponse(out, "Error al eliminar el estudiante");
        } finally {
            em.close();
        }
    }

    private void changePassword(HttpServletRequest request, PrintWriter out) {
        EntityManager em = emf.createEntityManager();
        try {
            HttpSession session = request.getSession();
            Long userId = (Long) session.getAttribute("userId"); // Asumiendo que el ID del usuario está en la sesión

            if (userId == null) {
                sendErrorResponse(out, "Usuario no autenticado");
                return;
            }

            String currentPassword = request.getParameter("currentPassword");
            String newPassword = request.getParameter("newPassword");

            em.getTransaction().begin();

            Estudiante estudiante = em.find(Estudiante.class, userId);
            if (estudiante != null) {
                // Verificar contraseña actual
                if (!estudiante.getPassEstd().equals(currentPassword)) {
                    em.getTransaction().rollback();
                    sendErrorResponse(out, "La contraseña actual es incorrecta");
                    return;
                }

                // Actualizar contraseña
                estudiante.setPassEstd(newPassword);
                em.merge(estudiante);
                em.getTransaction().commit();

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Contraseña cambiada exitosamente");

                Gson gson = new Gson();
                out.print(gson.toJson(response));
            } else {
                em.getTransaction().rollback();
                sendErrorResponse(out, "Usuario no encontrado");
            }

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            sendErrorResponse(out, "Error al cambiar la contraseña");
        } finally {
            em.close();
        }
    }

    private void sendErrorResponse(PrintWriter out, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);

        Gson gson = new Gson();
        out.print(gson.toJson(response));
    }
}
