<?php
/* @var $this SiteController */

$this->pageTitle=Yii::app()->name.' -  Statistika';
?>


<h1>Statistika</h1>

<?php Yii::app()->clientScript->registerCoreScript("jquery"); ?>
<?php $meritve = Meritev::model()->findAll(array('order'=>'CREATED ASC')); ?>

<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
    <script type="text/javascript">
      google.charts.load('current', {packages: ['corechart', 'line']});
google.charts.setOnLoadCallback(drawLineColors);

function drawLineColors() {
      var data = new google.visualization.DataTable();
      data.addColumn('number', 'Datum');
      data.addColumn('number', 'Temperature');

      data.addRows([
        <?php 
          foreach ($meritve as $meritev) {
            echo '['.$meritev->ID.','.$meritev->VALUE.'], ';
          }
          echo '[10, 20]';
        ?>
        //[0, 0], [1, 10]
      ]);

      var options = {
        hAxis: {
          title: 'Time'
        },
        vAxis: {
          title: 'Popularity'
        },
        colors: ['#a52714', '#097138']
      };

      var chart = new google.visualization.LineChart(document.getElementById('chart_div'));
      chart.draw(data, options);
    }
    </script>

  <div id="chart_div"></div>