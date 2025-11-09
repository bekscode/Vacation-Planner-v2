package com.romang.vacationplanner.database;

import android.app.Application;

import com.romang.vacationplanner.dao.ExcursionDAO;
import com.romang.vacationplanner.dao.UserDAO;
import com.romang.vacationplanner.dao.VacationDAO;
import com.romang.vacationplanner.entities.Excursion;
import com.romang.vacationplanner.entities.User;
import com.romang.vacationplanner.entities.Vacation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;

public class Repository {
    private VacationDAO mVacationDAO;
    private ExcursionDAO mExcursionDAO;
    private UserDAO mUserDAO;
    private List<Vacation> mAllVacations;
    private List<Excursion> mAllExcursions;
    private List<Excursion> mAssociatedExcursions;

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public Repository(Application application) {
        VacationDatabaseBuilder db = VacationDatabaseBuilder.getDatabase(application);
        mVacationDAO = db.vacationDAO();
        mExcursionDAO = db.excursionDAO();
        mUserDAO = db.userDAO();
    }

    // ==================== VACATION METHODS ====================

    public Vacation getVacationById(int vacationID) {
        for (Vacation v : getmAllVacations()) {
            if (v.getVacationID() == vacationID) {
                return v;
            }
        }
        return null;
    }

    public List<Vacation> getmAllVacations() {
        databaseExecutor.execute(() -> {
            mAllVacations = mVacationDAO.getAllVacations();
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return mAllVacations;
    }

    public void insert(Vacation vacation) {
        databaseExecutor.execute(() -> {
            mVacationDAO.insert(vacation);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Vacation vacation) {
        databaseExecutor.execute(() -> {
            mVacationDAO.update(vacation);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Vacation vacation) {
        databaseExecutor.execute(() -> {
            mVacationDAO.delete(vacation);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // ==================== EXCURSION METHODS ====================

    public List<Excursion> getmAllExcursions() {
        databaseExecutor.execute(() -> {
            mAllExcursions = mExcursionDAO.getAllExcursions();
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return mAllExcursions;
    }

    public List<Excursion> getmAssociatedExcursions(int vacationID) {
        databaseExecutor.execute(() -> {
            mAssociatedExcursions = mExcursionDAO.getAssociatedExcursions(vacationID);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return mAssociatedExcursions;
    }

    public void insert(Excursion excursion) {
        databaseExecutor.execute(() -> {
            mExcursionDAO.insert(excursion);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Excursion excursion) {
        databaseExecutor.execute(() -> {
            mExcursionDAO.update(excursion);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Excursion excursion) {
        databaseExecutor.execute(() -> {
            mExcursionDAO.delete(excursion);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // ==================== USER METHODS ====================


    public void insertUser(User user) {
        databaseExecutor.execute(() -> mUserDAO.insert(user));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public void updateUser(User user) {
        databaseExecutor.execute(() -> mUserDAO.update(user));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public void deleteUser(User user) {
        databaseExecutor.execute(() -> mUserDAO.delete(user));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public User getUserByUsername(String username) {
        try {
            Future<User> future = databaseExecutor.submit(new Callable<User>() {
                @Override
                public User call() {
                    return mUserDAO.getUserByUsername(username);
                }
            });
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public User getUserById(int userId) {
        try {
            Future<User> future = databaseExecutor.submit(new Callable<User>() {
                @Override
                public User call() {
                    return mUserDAO.getUserById(userId);
                }
            });
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public List<User> getAllUsers() {
        try {
            Future<List<User>> future = databaseExecutor.submit(new Callable<List<User>>() {
                @Override
                public List<User> call() {
                    return mUserDAO.getAllUsers();
                }
            });
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
