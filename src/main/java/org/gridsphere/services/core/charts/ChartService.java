package org.gridsphere.services.core.charts;

import java.io.File;
import java.io.IOException;

import org.gridsphere.services.core.charts.ChartDescriptor;
import org.gridsphere.portlet.service.PortletService;
import org.gridsphere.services.core.secdir.FileLocationID;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * @author <a href="mailto:tkucz@icis.pcz.pl">Tomasz Kuczynski</a>
 * @version $Id$
 */

public interface ChartService extends PortletService {
    public FileLocationID createChartLocationID(String userID, String category, String fileName);
    public String getChartUrl(FileLocationID fileLocationID);
    public String getDownloadChartUrl(FileLocationID fileLocationID, String saveAs);
    public ChartDescriptor createPieChart(FileLocationID fileLocationID, DefaultPieDataset dataset) throws IOException, Exception;
    public ChartDescriptor createPie3DChart(FileLocationID fileLocationID, DefaultPieDataset dataset) throws IOException, Exception;
    public ChartDescriptor createBarChart(FileLocationID fileLocationID, DefaultCategoryDataset dataset) throws IOException, Exception;
    public ChartDescriptor createBar3DChart(FileLocationID fileLocationID, DefaultCategoryDataset dataset) throws IOException, Exception;
    public ChartDescriptor createStackedBarChart(FileLocationID fileLocationID, DefaultCategoryDataset dataset) throws IOException, Exception;
    public ChartDescriptor createStackedBar3DChart(FileLocationID fileLocationID, DefaultCategoryDataset dataset) throws IOException, Exception;
    public ChartDescriptor createAreaChart(FileLocationID fileLocationID, DefaultCategoryDataset dataset) throws IOException, Exception;
    public ChartDescriptor createStackedAreaChart(FileLocationID fileLocationID, DefaultCategoryDataset dataset) throws IOException, Exception;
    public ChartDescriptor createLineChart(FileLocationID fileLocationID, DefaultCategoryDataset dataset) throws IOException, Exception;
    public ChartDescriptor createGanttChart(FileLocationID fileLocationID, TaskSeriesCollection dataset) throws IOException, Exception;
    public ChartDescriptor createBarXYChart(FileLocationID fileLocationID, XYSeriesCollection dataset) throws IOException, Exception;
    public ChartDescriptor createAreaXYChart(FileLocationID fileLocationID, XYSeriesCollection dataset) throws IOException, Exception;
    public ChartDescriptor createLineXYChart(FileLocationID fileLocationID, XYSeriesCollection dataset) throws IOException, Exception;
    public ChartDescriptor createStepAreaXYChart(FileLocationID fileLocationID, XYSeriesCollection dataset) throws IOException, Exception;
    public ChartDescriptor createBarXYChart(FileLocationID fileLocationID, TimeSeriesCollection dataset) throws IOException, Exception;
    public ChartDescriptor createTimeSeriesChart(FileLocationID fileLocationID, TimeSeriesCollection dataset) throws IOException, Exception;
    public ChartDescriptor createStepXYChart(FileLocationID fileLocationID, TimeSeriesCollection dataset) throws IOException, Exception;
    public void setChartDataset(FileLocationID fileLocationID, Dataset inDataset) throws IOException, Exception;
    public void setChartDataset(FileLocationID fileLocationID, Dataset inDataset, long datasetTimeStamp) throws IOException, Exception;
    public ChartDescriptor getChartDescriptor(FileLocationID fileLocationID) throws IOException, Exception;
    public void setChartDescriptor(FileLocationID fileLocationID, ChartDescriptor inChartDescriptor) throws IOException, Exception;
    public void setChartTitle(FileLocationID fileLocationID, String title) throws IOException, Exception;
    
    public String[] getChartList(FileLocationID fileLocationID);
    public File getChartImageFile(FileLocationID fileLocationID) throws IOException, Exception;
    public File getChartDataFile(FileLocationID fileLocationID) throws IOException, Exception;
    public boolean deleteChart(FileLocationID fileLocationID) throws IOException, Exception;
    public boolean deleteChart(FileLocationID fileLocationID, boolean deleteDataset) throws IOException, Exception;
    public boolean chartExists(FileLocationID fileLocationID);    
}
