import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
    public class TaskManager {
        Integer idTask = 0;
        Integer idEpic=0;
        Integer idSubtask=0;
        HashMap<Integer,Task> task = new HashMap<>();
        HashMap<Integer,Epic> epic = new HashMap<>();
        HashMap<Integer,Subtask> subtask = new HashMap<>();

        public Task createTask(Task task){
            idTask++;
            this.task.put(idTask,task);
            task.setId(idTask);
            return task;
        }
        public Epic createEpic(Epic epic){
            idEpic++;
            this.epic.put(idEpic,epic);
            epic.setId(idEpic);
            return epic;
        }
        public Subtask createSubtask(Subtask subtask){
            idSubtask++;
            subtask.setId(idSubtask);
            subtask.setEpicId(idEpic);
            if(epic.containsKey(idEpic)){
                Epic Objects = epic.get(idEpic);
                ArrayList<Subtask> subtask1 = Objects.getSubtasks();
                subtask1.add(subtask);
                Objects.setSubtasks(subtask1);

            }
            this.subtask.put(idSubtask,subtask);
            return subtask;
        }

        public void updateTask(Task task){
            for(Task idTask: this.task.values()){
                if(Objects.equals(idTask.getName(), task.getName())){
                    task.setId(idTask.getId());
                    this.task.put(task.getId(),task);
                }
            }
        }
        public void updateEpic(Epic epic){
            for(Epic idEpic: this.epic.values()){
                if(Objects.equals(idEpic.getName(),epic.getName())){
                    epic.setId(idEpic.getId());
                    ArrayList<Subtask> sub = idEpic.getSubtasks();
                    for(Subtask subtask1: sub){

                        short subtaskNew = 0;
                        short subtaskDone = 0;
                        if(subtask1.getStatus()==Status.IN_PROGRESS){
                            epic.setStatus(Status.IN_PROGRESS);
                            break;
                        }else if(subtask1.getStatus()==Status.NEW){
                            subtaskNew++;
                        } else if (subtask1.getStatus()==Status.DONE) {
                            subtaskDone++;
                        }
                        if(subtaskDone==sub.size()){
                            epic.setStatus(Status.DONE);
                            break;
                        }
                        if(subtaskNew==sub.size()){
                            epic.setStatus(Status.NEW);
                        }else{
                            epic.setStatus(Status.IN_PROGRESS);
                        }
                        break;
                    }
                    epic.setSubtasks(sub);
                    this.epic.put(epic.getId(), epic);
                }
            }
        }
        public void updateSubtask(Subtask subtask){
            for(Subtask idSubtask : this.subtask.values()){
                if(Objects.equals(idSubtask.getName(),subtask.getName())){
                    subtask.setId(idSubtask.getId());
                    subtask.setEpicId(idSubtask.getEpicId());
                    Epic Object = epic.get(subtask.getEpicId());
                    ArrayList<Subtask> subtask1 = Object.getSubtasks();
                    subtask1.set(subtask1.indexOf(idSubtask),subtask);
                    Object.setSubtasks(subtask1);
                    this.subtask.put(subtask.getId(), subtask);
                    updateEpic(Object);
                }
            }
        }
        public void clearTask(){
            task.clear();
            System.out.println("Список задач был успешно удален");
        }
        public void clearSubtask(){
            subtask.clear();
            System.out.println("Список подзадач был успешно удален");
        }
        public void clearEpic(){
            epic.clear();
            System.out.println("Список эпиков был успешно удален");
        }

        public void removeIdTask(int id){
            task.remove(id);
            System.out.println("Задача №"+id+" успешно удалена");
        }
        public void removeIdSubtask(int id){
            subtask.remove(id);
            System.out.println("Подзадача №"+id+" успешно удалена");
        }
        public void removeIdEpic(int id){
            epic.remove(id);
            System.out.println("Эпик №"+id+" успешно удалена");
        }

        public void printTask(){
            for(Task tasks : task.values()){
                print(tasks);
            }
        }
        public void printEpic(){
            for(Epic epics : epic.values()){
                print(epics);
                ArrayList<Subtask> subtasks = epics.getSubtasks();
                for(Subtask subtask : subtasks){
                    print(subtask);
                }
            }
        }


        private void print(Task task){
            System.out.println("№ "+task.getId());
            System.out.println("Задача: "+task.getName());
            System.out.println("Описание: "+task.getDescription());
            if(task.getStatus()==Status.NEW){
                System.out.println("Статус: Новый");
            }else if(task.getStatus()==Status.DONE){
                System.out.println("Статус: Выполнено");
            } else {
                System.out.println("Статус: В процессе");
            }
            System.out.println(" ");
            System.out.println(" ");
        }

    }

