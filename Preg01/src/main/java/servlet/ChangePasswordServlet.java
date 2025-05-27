package servlet;

import com.google.gson.Gson;
import dto.Estudiante;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "ChangePasswordServlet", urlPatterns = {"/ChangePasswordServlet"})
public class ChangePasswordServlet extends HttpServlet {

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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            HttpSession session = request.getSession();
            Long userId = (Long) session.getAttribute("userId");
            
            if (userId == null) {
                sendErrorResponse(out, "Usuario no autenticado");
                return;
            }
            
            String currentPassword = request.getParameter("currentPassword");
            String newPassword = request.getParameter("newPassword");
            
            if (currentPassword == null || currentPassword.trim().isEmpty() ||
                newPassword == null || newPassword.trim().isEmpty()) {
                sendErrorResponse(out, "Todos los campos son requeridos");
                return;
            }
            
            if (newPassword.length() < 6) {
                sendErrorResponse(out, "La nueva contraseña debe tener al menos 6 caracteres");
                return;
            }
            
            EntityManager em = emf.createEntityManager();
            try {
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
                    
                    Map<String, Object> responseMap = new HashMap<>();
                    responseMap.put("success", true);
                    responseMap.put("message", "Contraseña cambiada exitosamente");
                    
                    Gson gson = new Gson();
                    out.print(gson.toJson(responseMap));
                } else {
                    em.getTransaction().rollback();
                    sendErrorResponse(out, "Usuario no encontrado");
                }
                
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw e;
            } finally {
                em.close();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(out, "Error interno del servidor");
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